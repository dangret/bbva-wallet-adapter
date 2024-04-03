package com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.impl;

import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.BbvaWalletXmlAdapter;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Account;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.AccountRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.RecordRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BbvaWalletXmlAdapterImpl implements BbvaWalletXmlAdapter {

    private final EntryService entryService;
    private final AccountRepository accountRepository;

    int BBVA_CC_START_ROW_INDEX = 3;
    int BBVA_DC_START_ROW_INDEX = 4;
    int WALLET_START_ROW_INDEX = 1;

    private final RecordRepository recordRepository;

    @Override
    public List<Record> importFromCreditCardBbva(File fileLocation) {
        List<Record> entries = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();
            Account account = null;
            Row row = null;
            Record record = null;

            List<Account> accountEntities = accountRepository.findAll();

            for (int rowIndex = BBVA_CC_START_ROW_INDEX; rowIndex < sheet.read().size(); rowIndex++) {
                row = sheet.read().get(rowIndex);
                String rowValue = row.getCell(0).getRawValue();

                if (rowValue != null) {
                    if (rowValue.matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d") ) {
                        record = Record.builder()
                                .account(account)
                                .date(LocalDate.parse(row.getCell(0).getRawValue(), Util.DATE_FORMAT))
                                .note(row.getCell(1).asString())
                                .amount(new BigDecimal(
                                        Optional.ofNullable(row.getCell(2).getRawValue() != null
                                                        ? "-" + row.getCell(2).getRawValue() : null)
                                                .orElse(row.getCell(3).getRawValue()))
                                )
                                .build();
                        entries.add(record);
                    } else {
                        account = accountEntities.stream()
                                .filter(accountEntry ->
                                        rowValue.contains(accountEntry.getCardLastDigits()))
                                .findFirst().orElse(null);
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entries;
    }

    public List<Record> importFromDebitCardBbva(File fileLocation) {
        List<Record> entries = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();
            Account account = null;
            Row row = null;
            Record record = null;

            account = accountRepository.findById("30").orElseThrow();

            for (int rowIndex = BBVA_DC_START_ROW_INDEX; rowIndex < sheet.read().size(); rowIndex++) {
                row = sheet.read().get(rowIndex);
                if (!row.hasCell(0)) break;
                String rowValue = row.getCell(0).getRawValue();

                if (rowValue != null && rowValue.matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d")) {
                    record = Record.builder()
                            .account(account)
                            .date(LocalDate.parse(row.getCell(0).getRawValue(), Util.DATE_FORMAT))
                            .note(row.getCell(1).getRawValue())
                            .amount(new BigDecimal(
                                    Optional.ofNullable(row.getCell(2).getRawValue() != null && !row.getCell(2).getRawValue().isEmpty()
                                                    ? row.getCell(2).getRawValue().replace(",", "") : null)
                                            .orElse(row.getCell(3).getRawValue().replace(",","")))
                            )
                            .build();
                    entries.add(record);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entries;
    }

    @Override
    public File exportToWallet(String accountName) throws IOException {

        Account account = accountRepository.findByName(accountName).stream().findFirst().orElseThrow(IllegalArgumentException::new);
        List<Record> records = account.getRecords();
        records = records.stream().filter(record -> record.getExported() == null).toList();
        if (records.size() == 0) {
            return null;
        }
        File file = new File(String.format("%s_from_%s_to_%s.xlsx", account.getName(), records.get(0).getDate(), records.get(records.size()-1).getDate()));
        file.createNewFile();
        try (OutputStream os = new FileOutputStream(file); Workbook wb = new Workbook(os, "My app", "1.0")) {
            Worksheet ws = wb.newWorksheet("Sheet 1");
            ws.value(0, 0, "date");
            ws.value(0, 1, "notes");
            ws.value(0, 2, "amount");
            for (int row = 1; row < records.size(); row++) {
                ws.value(row, 0, records.get(row).getDate());
                ws.value(row, 1, records.get(row).getNote());
                ws.value(row, 2, records.get(row).getAmount());
            }
        }

        entryService.markAsExported(records);
        return file;
    }

    @Override
    public void exportToWallet() throws IOException {

        List<Record> records = recordRepository.findByExportedNull();
        records = records.stream().filter(record -> record.getExported() == null).toList();
        if (records.isEmpty()) {
            return;
        }

        Map<String, List<Record>> accountRecordMap = records.stream().collect(Collectors.groupingBy(record -> record.getAccount().getName()));
        File file;
        for (Map.Entry<String, List<Record>> entry : accountRecordMap.entrySet()) {
            String account = entry.getKey();
            List<Record> accountRecords = entry.getValue();
            String maxDate = accountRecords.stream().map(Record::getDate).max(LocalDate::compareTo).get().toString();
            String minDate = accountRecords.stream().map(Record::getDate).min(LocalDate::compareTo).get().toString();
            file = new File(String.format("%s_from_%s_to_%s.xlsx", account, minDate, maxDate));
            file.createNewFile();
            try (OutputStream os = new FileOutputStream(file); Workbook wb = new Workbook(os, "My app", "1.0")) {
                Worksheet ws = wb.newWorksheet("Sheet 1");
                ws.value(0, 0, "date");
                ws.value(0, 1, "notes");
                ws.value(0, 2, "amount");
                for (int row = 1; row < accountRecords.size(); row++) {
                    ws.value(row, 0, accountRecords.get(row).getDate());
                    ws.value(row, 1, accountRecords.get(row).getNote());
                    ws.value(row, 2, accountRecords.get(row).getAmount());
                }
            }
        }

        entryService.markAsExported(records);
    }

    @Override
    public List<Record> importFromWallet(File fileLocation) {
        List<Record> entries = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();

            Row row = null;
            String account = null;
            Record record = null;
            for (int rowIndex = WALLET_START_ROW_INDEX; rowIndex < sheet.read().size(); rowIndex++) {
                row = sheet.read().get(rowIndex);
                String rowValue = row.getCell(0).getRawValue();

                if (rowValue == null) break;

                record = Record.builder()
                        .account(accountRepository.findByName(row.getCell(0).getRawValue()).stream()
                                .findFirst()
                                .orElse(null))
                        .date(LocalDate.parse(row.getCell(9).getRawValue(), Util.DATE_FORMAT))
                        .note(row.getCell(8).asString())
                        .amount(new BigDecimal(row.getCell(3).getRawValue()))
                        .exported(LocalDate.now())
                        .build();
                entries.add(record);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        entryService.update(entries);
        return entries;
    }


}

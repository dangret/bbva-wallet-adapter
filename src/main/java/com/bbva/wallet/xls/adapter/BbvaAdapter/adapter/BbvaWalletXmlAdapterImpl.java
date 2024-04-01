package com.bbva.wallet.xls.adapter.BbvaAdapter.adapter;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Account;
import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.impl.BbvaWalletXmlAdapter;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.AccountEntity;
import com.bbva.wallet.xls.adapter.BbvaAdapter.mapper.AccountMapper;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.AccountRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class BbvaWalletXmlAdapterImpl implements BbvaWalletXmlAdapter {

    private final EntryService entryService;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    int BBVA_START_ROW_INDEX = 3;
    int WALLET_START_ROW_INDEX = 1;

    String tdd = "1234";
    String tdc = "5678";
    @Override
    public List<Record> importFromBbva(File fileLocation) {
        List<Record> entries = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();
            AccountEntity account = null;
            Row row = null;
            Record record = null;

            List<AccountEntity> accountEntities = accountRepository.findAll();

            for (int rowIndex = BBVA_START_ROW_INDEX; rowIndex < sheet.read().size(); rowIndex++) {
                row = sheet.read().get(rowIndex);
                String rowValue = row.getCell(0).getRawValue();

                if (rowValue != null) {
                    if (rowValue.matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d") ) {
                        record = Record.builder()
                                .account(accountMapper.toAccount(account))
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

    @Override
    public File exportToWallet(String account) throws IOException {

        List<Record> entries = entryService.getNotExportedEntries();
        entries = entries.stream().filter(entry -> entry.getAccount().equals(account)).toList();

        File file = new File(String.format("from_%s_to_%s.xlsx", entries.get(0).getDate(), entries.get(entries.size()-1).getDate()));
        try (OutputStream os = new FileOutputStream(file); Workbook wb = new Workbook(os, "My app", "1.0")) {
            Worksheet ws = wb.newWorksheet("Sheet 1");
            for (int row = 0; row < entries.size(); row++) {
                ws.value(row, 0, entries.get(row).getDate());
                ws.value(row, 0, entries.get(row).getNote());
                ws.value(row, 0, entries.get(row).getAmount());
            }
        }
        entryService.markAsExported(entries);
        return file;
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
                        .account(accountMapper.toAccount(
                                accountRepository.findByName(row.getCell(0).getRawValue()).stream().findFirst().orElse(null)))
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

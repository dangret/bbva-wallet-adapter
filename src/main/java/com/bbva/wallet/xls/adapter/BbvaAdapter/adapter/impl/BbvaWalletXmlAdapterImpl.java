package com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.impl;

import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.BbvaWalletXmlAdapter;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Account;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.AccountRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.RecordRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util.*;

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
    try (FileInputStream file = new FileInputStream(fileLocation); Workbook wb = new XSSFWorkbook(file)) {
      Sheet sheet = wb.getSheetAt(0);
      Account account = null;
      Row row = null;
      Record record = null;

      List<Account> accountEntities = accountRepository.findAll();

      for (int rowIndex = BBVA_CC_START_ROW_INDEX; rowIndex < sheet.getLastRowNum(); rowIndex++) {
        row = sheet.getRow(rowIndex);
        String rowValue = row.getCell(0).toString();

        if (rowValue != null) {
          if (rowValue.matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d")) {
            Row finalRow = row;
            record = Record.builder()
                .account(account)
                .date(LocalDate.parse(row.getCell(DATE_COLUMN_INDEX).toString(), Util.DATE_FORMAT_BBVA)
                    .atTime(0, 0).atZone(ZoneId.systemDefault()).toOffsetDateTime())
                .note(row.getCell(1).toString())
                .amount(new BigDecimal(
                    Optional.ofNullable(!row.getCell(2).toString().isEmpty()
                            ? "-" + row.getCell(2).toString() : null)
                        .orElseGet(() -> finalRow.getCell(3).toString()))
                )
                .build();
            record.setId(Util.calculateId(record));
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
    try (FileInputStream file = new FileInputStream(fileLocation); Workbook wb = new XSSFWorkbook(file)) {
      Sheet sheet = wb.getSheetAt(0);
      Account account = null;
      Row row = null;
      Record record = null;

      account = accountRepository.findById("30").orElseThrow();

      for (int rowIndex = BBVA_DC_START_ROW_INDEX; rowIndex < sheet.getLastRowNum(); rowIndex++) {
        row = sheet.getRow(rowIndex);
        Cell cell = row.getCell(DATE_COLUMN_INDEX);
        if (cell == null || cell.toString().isEmpty()) break;
        String cellValue = cell.toString();

        if (cellValue.matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d")) {
          record = Record.builder()
              .account(account)
              .date(LocalDate.parse(row.getCell(DATE_COLUMN_INDEX).toString(), Util.DATE_FORMAT_BBVA)
                  .atTime(0, 0).atZone(ZoneId.systemDefault()).toOffsetDateTime())
              .note(row.getCell(NOTES_COLUMN_INDEX).toString())
              .amount(new BigDecimal(
                  Optional.ofNullable(row.getCell(AMOUNT_COLUMN_INDEX).toString() != null && !row.getCell(AMOUNT_COLUMN_INDEX).toString().isEmpty()
                          ? row.getCell(AMOUNT_COLUMN_INDEX).toString().replace(",", "") : null)
                      .orElse(row.getCell(3).toString().replace(",", "")))
              )
              .build();
          record.setId(Util.calculateId(record));
          entries.add(record);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    print(entries);

    return entries;
  }

  private void print(List<Record> entries) {
    System.out.println();
    for (Record entry : entries) {
      System.out.flush();
      System.out.printf("%20s | %10s | %10s | %4s%n",
          entry.getNote(), entry.getDate().format(DATE_TIME_FORMAT_WALLET), entry.getAmount(), entry.getId());
      System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
    }
    System.out.println();
  }

  @Override
  public File exportToWallet(String accountName) throws IOException {

    Account account = accountRepository.findByName(accountName).stream().findFirst().orElseThrow(IllegalArgumentException::new);
    List<Record> records = account.getRecords();
    records = records.stream().filter(record -> record.getExported() == null).toList();
    if (records.size() == 0) {
      return null;
    }
    Workbook workbook = new XSSFWorkbook();

    Sheet sheet = workbook.createSheet("Persons");
    File file = new File(String.format("%s_from_%s_to_%s.xlsx", account.getName(), records.get(0).getDate(), records.get(records.size() - 1).getDate()));
    file.createNewFile();

    try (OutputStream os = new FileOutputStream(file); Workbook wb = new XSSFWorkbook()) {
      Sheet ws = wb.createSheet();
      short dateFormatIndex = 15;
      short notesFormatIndex = 0;
      short amountFormatIndex = 4;

      Row row = ws.createRow(0);
      row.createCell(0).setCellValue("date");
      row.createCell(1).setCellValue("notes");
      row.createCell(2).setCellValue("amount");
      for (int rowIndex = 1; rowIndex < records.size(); rowIndex++) {
        row = ws.createRow(rowIndex);
        Cell dateCell = row.createCell(DATE_COLUMN_INDEX);
        Cell noteCell = row.createCell(NOTES_COLUMN_INDEX);
        Cell amountCell = row.createCell(AMOUNT_COLUMN_INDEX);

        dateCell.getCellStyle().setDataFormat(dateFormatIndex);
        dateCell.setCellValue(records.get(rowIndex).getDate().format(DateTimeFormatter.ISO_DATE));
        noteCell.getCellStyle().setDataFormat(notesFormatIndex);
        noteCell.setCellValue(records.get(rowIndex).getNote());
        amountCell.getCellStyle().setDataFormat(amountFormatIndex);
        amountCell.setCellValue(records.get(rowIndex).getAmount().doubleValue());
      }
      wb.write(os);
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
      String maxDate = accountRecords.stream().map(Record::getDate).max(OffsetDateTime::compareTo).get().format(DateTimeFormatter.ISO_DATE);
      String minDate = accountRecords.stream().map(Record::getDate).min(OffsetDateTime::compareTo).get().format(DateTimeFormatter.ISO_DATE);
      file = new File(String.format("%s_from_%s_to_%s.xlsx", account, minDate, maxDate));
      file.createNewFile();
      try (OutputStream os = new FileOutputStream(file); Workbook wb = new XSSFWorkbook()) {
        Sheet ws = wb.createSheet();
        short dateFormatIndex = 15;
        short notesFormatIndex = 0;
        short amountFormatIndex = 4;

        Row row = ws.createRow(0);
        row.createCell(0).setCellValue("date");
        row.createCell(1).setCellValue("note");
        row.createCell(2).setCellValue("amount");

        for (int rowIndex = 1; rowIndex <= accountRecords.size(); rowIndex++) {
          row = ws.createRow(rowIndex);
          Cell dateCell = row.createCell(DATE_COLUMN_INDEX);
          Cell noteCell = row.createCell(NOTES_COLUMN_INDEX);
          Cell amountCell = row.createCell(AMOUNT_COLUMN_INDEX);

          dateCell.getCellStyle().setDataFormat(dateFormatIndex);
          dateCell.setCellValue(accountRecords.get(rowIndex - 1).getDate().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime().format(DATE_TIME_FORMAT_WALLET));
          noteCell.getCellStyle().setDataFormat(notesFormatIndex);
          noteCell.setCellValue(accountRecords.get(rowIndex - 1).getNote());
          amountCell.getCellStyle().setDataFormat(amountFormatIndex);
          amountCell.setCellValue(accountRecords.get(rowIndex - 1).getAmount().doubleValue());
        }
        wb.write(os);
        print(accountRecords);
      }
    }

    entryService.markAsExported(records);
  }

  @Override
  public List<Record> importFromWallet(File fileLocation) {
    List<Record> entries = new ArrayList<>();
    try (FileInputStream file = new FileInputStream(fileLocation); Workbook wb = new HSSFWorkbook(file)) {
      Sheet sheet = wb.getSheetAt(0);

      Row row = null;
      String account = null;
      Record record = null;
      for (int rowIndex = WALLET_START_ROW_INDEX; rowIndex < sheet.getLastRowNum(); rowIndex++) {
        row = sheet.getRow(rowIndex);
        String rowValue = row.getCell(0).toString();

        if (rowValue == null || rowValue.isEmpty()) break;

        record = Record.builder()
            .account(accountRepository.findByName(row.getCell(0).toString()).stream()
                .findFirst()
                .orElse(null))
            .date(LocalDate.parse(row.getCell(9).toString(), DATE_FORMAT_WALLET)
                .atTime(0, 0, 0).atOffset(ZoneOffset.UTC))
            .note(row.getCell(8).toString())
            .amount(new BigDecimal(row.getCell(3).toString()))
            .exported(OffsetDateTime.now(ZoneOffset.UTC))
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

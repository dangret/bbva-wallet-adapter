package com.bbva.wallet.xls.adapter.BbvaAdapter.adapter;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.impl.BbvaWalletXmlAdapter;
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
    int START_ROW_INDEX = 3;

    String tdd = "1234";
    String tdc = "5678";
    @Override
    public List<Entry> read(File fileLocation) {
        List<Entry> entries = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();

            Row row = null;
            String account = null;
            Entry entry = null;
            for (int rowIndex = START_ROW_INDEX; rowIndex < sheet.read().size(); rowIndex++) {
                row = sheet.read().get(rowIndex);
                if (rowIndex == START_ROW_INDEX && row.getCell(0).getRawValue().contains(tdd)){
                    account = tdd;
                    continue;
                }
                String rowValue = row.getCell(0).getRawValue();

                if (rowValue != null && rowValue.matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d")) {
                    entry = Entry.builder()
                            .account(account)
                            .date(LocalDate.parse(row.getCell(0).getRawValue(), Util.DATE_FORMAT))
                            .description(row.getCell(1).asString())
                            .amount(new BigDecimal(
                                    Optional.ofNullable(row.getCell(2).getRawValue() != null
                                                    ? "-" + row.getCell(2).getRawValue() : null)
                                            .orElse(row.getCell(3).getRawValue()))
                            )
                            .build();
                    entries.add(entry);
                } else {
                    if (rowValue != null && rowValue.contains(tdc)) {
                        account = tdc;
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entries;
    }

    @Override
    public File export(String account) throws IOException {

        List<Entry> entries = entryService.getNotExportedEntries();
        entries = entries.stream().filter(entry -> entry.getAccount().equals(account)).toList();

        File file = new File(String.format("from_%s_to_%s.xlsx", entries.get(0).getDate(), entries.get(entries.size()-1).getDate()));
        try (OutputStream os = new FileOutputStream(file); Workbook wb = new Workbook(os, "My app", "1.0")) {
            Worksheet ws = wb.newWorksheet("Sheet 1");
            for (int row = 0; row < entries.size(); row++) {
                ws.value(row, 0, entries.get(row).getDate());
                ws.value(row, 0, entries.get(row).getDescription());
                ws.value(row, 0, entries.get(row).getAmount());
            }
        }
        entryService.markAsExported(entries);
        return file;
    }
}

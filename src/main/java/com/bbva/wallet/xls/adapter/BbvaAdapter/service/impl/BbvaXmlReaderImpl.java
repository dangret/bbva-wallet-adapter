package com.bbva.wallet.xls.adapter.BbvaAdapter.service.impl;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.BbvaXmlReader;
import com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class BbvaXmlReaderImpl implements BbvaXmlReader {

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
}

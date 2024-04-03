package com.bbva.wallet.xls.adapter.BbvaAdapter.util;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Util {
    public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static String calculateId(Record record) {
        return UUID.nameUUIDFromBytes((record.getAmount().toString() + record.getNote() + record.getDate().toString()).getBytes())
                        .toString();

    }
}

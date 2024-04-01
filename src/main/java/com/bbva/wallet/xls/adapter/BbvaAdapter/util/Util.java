package com.bbva.wallet.xls.adapter.BbvaAdapter.util;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.EntryEntity;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Util {
    public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static String calculateId(Entry entry) {
        return UUID.nameUUIDFromBytes((entry.getAmount().toString() + entry.getNote() + entry.getDate().toString()).getBytes())
                        .toString();

    }

    public static String calculateId(EntryEntity entry) {
        return UUID.nameUUIDFromBytes((entry.getAmount().toString() + entry.getNote() + entry.getDate().toString()).getBytes())
                .toString();

    }
}

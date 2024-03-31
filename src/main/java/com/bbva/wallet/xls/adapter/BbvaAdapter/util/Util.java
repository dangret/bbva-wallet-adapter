package com.bbva.wallet.xls.adapter.BbvaAdapter.util;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Util {
    public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static String calculateId(Entry entry) {
        return UUID.nameUUIDFromBytes((entry.getAmount().toString() + entry.getDescription() + entry.getDate().toString()).getBytes())
                        .toString();

    }
}

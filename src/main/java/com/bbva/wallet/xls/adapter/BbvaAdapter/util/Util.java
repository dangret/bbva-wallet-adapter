package com.bbva.wallet.xls.adapter.BbvaAdapter.util;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;

import java.util.UUID;

public class Util {
    public static String calculateId(Entry entry) {
        return UUID.fromString(entry.getAmount().toString() + entry.getDescription() + entry.getDate().toString())
                        .toString();

    }
}

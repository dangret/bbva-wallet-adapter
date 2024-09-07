package com.bbva.wallet.xls.adapter.BbvaAdapter.util;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class Util {

    public static int DATE_COLUMN_INDEX = 0;
    public static int NOTES_COLUMN_INDEX = 1;
    public static int AMOUNT_COLUMN_INDEX = 2;
    public static DateTimeFormatter DATE_FORMAT_BBVA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static DateTimeFormatter DATE_FORMAT_WALLET = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public static DateTimeFormatter DATE_TIME_FORMAT_WALLET = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static long TZ_OFFSET_HOURS = (TimeZone.getDefault().getOffset(new Date().getTime())/1000/60/60) * -1;

    public static String calculateId(Record record) {
        return UUID.nameUUIDFromBytes(
                (record.getAmount().toString() +
                        record.getDate().atZoneSameInstant(ZoneOffset.UTC) +
                        record.getAccount().getId())
                        .getBytes())
                .toString();
    }
}

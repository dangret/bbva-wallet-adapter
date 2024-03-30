package com.bbva.wallet.xls.adapter.BbvaAdapter.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Entry {

    String id;
    BigDecimal amount;
    String description;
    Category category;
    LocalDate date;
    String account;
}

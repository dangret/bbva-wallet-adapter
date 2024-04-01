package com.bbva.wallet.xls.adapter.BbvaAdapter.dto;

import com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Account {

    private String id;
    private String name;
    private String cardLastDigits;
}

package com.bbva.wallet.xls.adapter.BbvaAdapter.dto;

import com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@ToString
public class Entry {

    private String id;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private String account;

    public String getId() {
        return this.id != null ? this.id : (this.id = Util.calculateId(this));
    }

    public void setId(String id) {
        this.id = Util.calculateId(this);
    }
}

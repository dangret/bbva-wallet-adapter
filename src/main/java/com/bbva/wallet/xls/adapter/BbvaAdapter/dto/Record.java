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
public class Record {

    private String id;
    private BigDecimal amount;
    private String note;
    private LocalDate date;
    private LocalDate exported;
    private Account account;
    /*private String category;
    private String currency;
    private BigDecimal ref_currency_amount;
    private String type;
    private String payment_type;
    private String payment_type_local;
    private double gps_latitude;
    private double gps_longitude;
    private double gps_accuracy_in_meters;
    private String warranty_in_month;
    private boolean transfer;
    private String payee;
    private String labels;
    private int envelope_id;
    private boolean custom_category;*/



    public String getId() {
        return this.id != null ? this.id : (this.id = Util.calculateId(this));
    }

    public void setId(String id) {
        this.id = Util.calculateId(this);
    }
}

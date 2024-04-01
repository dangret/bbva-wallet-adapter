package com.bbva.wallet.xls.adapter.BbvaAdapter.entity;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Account;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "record")
public class RecordEntity {

    @Id
    private String id;
    private BigDecimal amount;
    private String note;
    private LocalDate date;
    private LocalDate exported;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private AccountEntity account;
    /*
    private String category;
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

}

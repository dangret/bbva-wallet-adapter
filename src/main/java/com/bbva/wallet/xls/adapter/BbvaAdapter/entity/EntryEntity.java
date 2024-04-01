package com.bbva.wallet.xls.adapter.BbvaAdapter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "entry")
@Getter
@Setter
@RequiredArgsConstructor
public class EntryEntity {

    @Id
    /*@GeneratedValue(generator = "id-generator")
    @GenericGenerator(name = "id-generator", type = EntryIdGenerator.class )*/
    private String id;
    private BigDecimal amount;
    private String note;
    private LocalDate date;
    private LocalDate exported;
    private String account;
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

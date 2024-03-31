package com.bbva.wallet.xls.adapter.BbvaAdapter.entity;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.generator.EntryIdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "entry")
@Getter
public class EntryEntity {

    @Id
    @GeneratedValue(generator = "id-generator")
    @GenericGenerator(name = "id-generator", type = EntryIdGenerator.class )
    private String id;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private String account;
    private LocalDate exported;
}

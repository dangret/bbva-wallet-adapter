package com.bbva.wallet.xls.adapter.BbvaAdapter.entity;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;
    private String name;
    private String cardLastDigits;

}

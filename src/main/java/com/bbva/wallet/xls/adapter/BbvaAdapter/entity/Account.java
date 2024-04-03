package com.bbva.wallet.xls.adapter.BbvaAdapter.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
    private List<Record> records;

    private String name;
    private String cardLastDigits;

}

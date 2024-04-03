package com.bbva.wallet.xls.adapter.BbvaAdapter.repository;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByCardLastDigitsContaining(String lastDigits);
    List<Account> findByName(String name);
}

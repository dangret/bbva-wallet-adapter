package com.bbva.wallet.xls.adapter.BbvaAdapter.repository;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Account;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.AccountEntity;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    List<AccountEntity> findByCardLastDigitsContaining(String lastDigits);
    List<AccountEntity> findByName(String name);
}

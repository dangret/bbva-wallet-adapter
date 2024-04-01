package com.bbva.wallet.xls.adapter.BbvaAdapter.repository;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, String> {
    List<RecordEntity> findByExportedNull();
    List<RecordEntity> findByIdInAndExportedNotNull(Set<String> ids);
}

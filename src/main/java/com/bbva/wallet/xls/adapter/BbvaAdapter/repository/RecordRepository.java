package com.bbva.wallet.xls.adapter.BbvaAdapter.repository;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RecordRepository extends JpaRepository<Record, String> {
    List<Record> findByExportedNull();
    List<Record> findByIdInAndExportedNotNull(Set<String> ids);

}

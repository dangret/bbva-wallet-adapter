package com.bbva.wallet.xls.adapter.BbvaAdapter.repository;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.EntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EntryRepository extends JpaRepository<EntryEntity, String> {
    List<EntryEntity> findByExportedNull();
    List<EntryEntity> findByIdInAndExportedNull(Set<String> ids);
    List<EntryEntity> findByIdInAndExportedNotNull(Set<String> ids);
}

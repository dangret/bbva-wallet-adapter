package com.bbva.wallet.xls.adapter.BbvaAdapter.service.impl;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.EntryEntity;
import com.bbva.wallet.xls.adapter.BbvaAdapter.mapper.EntryMapper;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.EntryRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final EntryMapper entryMapper;
    @Override
    public void save(List<Entry> entries) {
        List<String> entryIds = entries.stream().map(Entry::getId).toList();
        List<Entry> newEntries = entries.stream()
                .filter(entry ->
                        Stream.concat(entryIds.stream(), entryRepository.findAllById(entryIds).stream()
                        .map(EntryEntity::getId).toList().stream()).distinct().toList()
                                .contains(entry.getId())).toList();

        entryRepository.saveAll(entryMapper.toEntities(newEntries));
    }

    @Override
    public List<Entry> getNotExportedEntries() {
        return entryMapper.toEntry(entryRepository.findByExportedNull());
    }

    @Override
    public void markAsExported(List<Entry> entries) {
        entries.forEach(entry -> entry.setExported(LocalDate.now()));
        entryRepository.saveAll(entryMapper.toEntities(entries));
    }

    // TODO: Blocked since there is not API in wallet
    public boolean export() {
        return false;
    }

}

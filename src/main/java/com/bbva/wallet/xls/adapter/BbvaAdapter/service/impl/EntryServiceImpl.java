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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final EntryMapper entryMapper;

    /***
     * Used to save from BBVA to DB, it doesn't matter exported status, just make sure don't get duplicates
     * @param entries
     */
    @Override
    public void save(List<Entry> entries) {
        List<String> entryIds = entries.stream()
                .map(Entry::getId)
                .toList();

        List<String> existingEntriesIds = entryRepository.findAllById(entryIds).stream()
                .map(EntryEntity::getId)
                .toList();

        List<Entry> newEntries = entries.stream()
                .filter(entry -> !existingEntriesIds.contains(entry.getId()))
                .toList();


        //TODO: verify why entryMapper is not mapping correctly.
        entryRepository.saveAll(entryMapper.toEntities(newEntries));
    }

    /***
     * Use it to export from wallet to DB, if exists also see if is exported, if both are true, then ignore, otherwise
     * save it to DB
     * @param entries
     */
    @Override
    public void update(List<Entry> entries) {
        Set<String> entryIds = entries.stream().map(Entry::getId).collect(Collectors.toSet());

        // Get all entries already in DB with exported status
        List<String> alreadyExportedEntryIds = entryRepository.findByIdInAndExportedNotNull(entryIds).stream()
                .map(EntryEntity::getId).toList();

        // Remove from read entries, those already in DB and already exported (means exported previously from wallet)
        List<Entry> newEntries = entries.stream()
                .filter(entry -> !alreadyExportedEntryIds.contains(entry.getId())).toList();

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

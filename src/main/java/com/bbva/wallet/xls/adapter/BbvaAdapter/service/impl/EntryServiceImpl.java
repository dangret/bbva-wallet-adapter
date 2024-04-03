package com.bbva.wallet.xls.adapter.BbvaAdapter.service.impl;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.RecordRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EntryServiceImpl implements EntryService {

    private final RecordRepository recordRepository;

    /***
     * Used to save from BBVA to DB, it doesn't matter exported status, just make sure don't get duplicates
     * @param entries
     */
    @Override
    public void save(List<Record> entries) {
        List<String> entryIds = entries.stream()
                .map(Record::getId)
                .toList();

        List<String> existingEntriesIds = recordRepository.findAllById(entryIds).stream()
                .map(Record::getId)
                .toList();

        List<Record> records = entries.stream()
                .filter(entry -> !existingEntriesIds.contains(entry.getId()))
                .toList();


        recordRepository.saveAll(records);
    }

    /***
     * Use it to export from wallet to DB, if exists also see if is exported, if both are true, then ignore, otherwise
     * save it to DB
     * @param entries
     */
    @Override
    public void update(List<Record> entries) {
        Set<String> entryIds = entries.stream().map(Record::getId).collect(Collectors.toSet());

        // Get all entries already in DB with exported status
        List<String> alreadyExportedEntryIds = recordRepository.findByIdInAndExportedNotNull(entryIds).stream()
                .map(Record::getId).toList();

        // Remove from read entries, those already in DB and already exported (means exported previously from wallet)
        List<Record> records = entries.stream()
                .filter(entry -> !alreadyExportedEntryIds.contains(entry.getId())).toList();

        recordRepository.saveAll(records);
    }

    @Override
    public List<Record> getNotExportedEntries() {
        return recordRepository.findByExportedNull();
    }

    @Override
    public void markAsExported(List<Record> records) {
        records.forEach(entry -> entry.setExported(LocalDate.now()));
        recordRepository.saveAll(records);
    }

    // TODO: Blocked since there is not API in wallet
    public boolean export() {
        return false;
    }

}

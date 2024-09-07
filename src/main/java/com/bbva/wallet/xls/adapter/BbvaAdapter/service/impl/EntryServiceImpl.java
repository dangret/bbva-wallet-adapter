package com.bbva.wallet.xls.adapter.BbvaAdapter.service.impl;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.RecordRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class EntryServiceImpl implements EntryService {

    private final RecordRepository recordRepository;

    /***
     * Used to save from BBVA to DB, it doesn't matter exported status, just make sure don't get duplicates
     * @param entries
     */
    @Override
    public void save(List<Record> entries) {

        List<String> idsInDB = recordRepository.findAllById(entries.stream().map(Record::getId).toList()).stream().map(Record::getId).toList();
        List<Record> entriesFiltered = entries.stream().filter(entry -> !idsInDB.contains(entry.getId())).toList();

        recordRepository.saveAll(entriesFiltered);
    }

    /***
     * Use it to export from wallet to DB, if exists also see if is exported, if both are true, then ignore, otherwise
     * save it to DB
     * @param entries
     */
    @Override
    public void update(final List<Record> entries) {
        List<String> entryIds = entries.stream().map(Record::getId).collect(Collectors.toList());

        // Get all entries already in DB with exported status
        List<Record> records = recordRepository.findByIdIn(entryIds).stream()
                .filter(record -> record.getExported() != null).toList();

        for (Record entry : entries) {
            Record record = records.stream().filter(r -> r.getId().equals(entry.getId())).findFirst().orElse(null);
            if (record == null) continue;
            entry.setExported(record.getExported());
        }

        recordRepository.saveAll(entries);
    }

    @Override
    public List<Record> getNotExportedEntries() {
        return recordRepository.findByExportedNull();
    }

    @Override
    public void markAsExported(List<Record> records) {
        records.forEach(entry -> entry.setExported(OffsetDateTime.now(ZoneOffset.UTC)));
        recordRepository.saveAll(records);
    }

    // TODO: Blocked since there is not API in wallet
    public boolean export() {
        return false;
    }

}

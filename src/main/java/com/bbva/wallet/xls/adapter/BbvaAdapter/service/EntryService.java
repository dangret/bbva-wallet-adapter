package com.bbva.wallet.xls.adapter.BbvaAdapter.service;


import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;

import java.util.List;

public interface EntryService {

    void save(List<Record> entries);
    void update(List<Record> entries);
    List<Record> getNotExportedEntries();
    void markAsExported(List<Record> entries);

}

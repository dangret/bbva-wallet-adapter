package com.bbva.wallet.xls.adapter.BbvaAdapter.service;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;

import java.util.List;

public interface EntryService {

    void save(List<Entry> entries);
    void update(List<Entry> entries);
    List<Entry> getNotExportedEntries();
    void markAsExported(List<Entry> entries);

}

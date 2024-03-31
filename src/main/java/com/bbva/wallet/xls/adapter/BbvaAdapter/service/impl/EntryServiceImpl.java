package com.bbva.wallet.xls.adapter.BbvaAdapter.service.impl;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
import com.bbva.wallet.xls.adapter.BbvaAdapter.mapper.EntryMapper;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.EntryRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final EntryMapper entryMapper;
    @Override
    public Boolean save(List<Entry> entries) {
        entryRepository.saveAll(entryMapper.toEntities(entries));
        return true;
    }
}

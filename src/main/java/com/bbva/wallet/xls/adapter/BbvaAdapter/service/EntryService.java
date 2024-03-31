package com.bbva.wallet.xls.adapter.BbvaAdapter.service;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;

import java.io.File;
import java.util.List;

public interface EntryService {

    void save(List<Entry> entries);

}

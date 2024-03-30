package com.bbva.wallet.xls.adapter.BbvaAdapter.service;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;

import java.io.FileInputStream;
import java.util.List;

public interface BbvaXmlReader {

    List<Entry> read(String fileLocation);

}

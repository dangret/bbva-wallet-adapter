package com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.impl;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface BbvaWalletXmlAdapter {

    List<Entry> read(File fileLocation);

    File export(String account) throws IOException;

}

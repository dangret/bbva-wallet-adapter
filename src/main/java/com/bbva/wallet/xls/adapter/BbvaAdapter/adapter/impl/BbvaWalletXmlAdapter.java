package com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.impl;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Record;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface BbvaWalletXmlAdapter {

    List<Record> importFromBbva(File fileLocation);
    File exportToWallet(String account) throws IOException;
    List<Record> importFromWallet(File fileLocation);


}

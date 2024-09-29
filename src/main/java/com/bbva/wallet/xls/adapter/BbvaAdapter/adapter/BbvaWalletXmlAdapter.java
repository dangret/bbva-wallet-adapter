package com.bbva.wallet.xls.adapter.BbvaAdapter.adapter;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;

import javax.naming.directory.InvalidAttributeValueException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface BbvaWalletXmlAdapter {

    List<Record> importFromCreditCardBbva(File fileLocation);

    List<Record> importFromDebitCardBbva(File fileLocation);
    File exportToWallet(String account) throws IOException;

    void exportToWallet() throws IOException;
    List<Record> importFromWallet(File fileLocation);

    void importToBbvaAll() throws InvalidAttributeValueException;

}

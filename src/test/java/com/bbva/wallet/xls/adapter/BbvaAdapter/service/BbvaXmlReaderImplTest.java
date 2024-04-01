package com.bbva.wallet.xls.adapter.BbvaAdapter.service;


import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.BbvaWalletXmlAdapterImpl;
import com.bbva.wallet.xls.adapter.BbvaAdapter.mapper.AccountMapper;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource("classpath:application.yaml")
public class BbvaXmlReaderImplTest {

    @InjectMocks
    BbvaWalletXmlAdapterImpl bbvaXmlReader;
    @Mock
    AccountRepository accountRepository;
    @Mock
    AccountMapper accountMapper;

    File file;

    @Autowired
    ResourceLoader resourceLoader;

    @Test
    public void RetrieveValidDataWhenGettingRow4Cell1() throws IOException {
        givenValidFile();
        assertInformationIsValid();
    }

    private void assertInformationIsValid() {
        List<Record> entries = bbvaXmlReader.importFromBbva(file);
        assertEquals("EMBOTELLADORA DEL NAYA", entries.get(0).getNote());
        assertEquals(new BigDecimal("-174"), entries.get(0).getAmount());
        assertEquals("3b6c655f-47ff-3964-8bdc-659c8ac35a82", entries.get(0).getId());

        assertEquals("SUFACEN MOLOLOA", entries.get(2).getNote());
        assertEquals(new BigDecimal("-280.96"), entries.get(2).getAmount());

        assertEquals("EMBOTELLADORA DEL NAYA", entries.get(3).getNote());
        assertEquals(new BigDecimal("222"), entries.get(3).getAmount());
    }

    private void givenValidFile() throws IOException {
        file = resourceLoader.getResource("classpath:data/testData.xlsx").getFile();
    }


}

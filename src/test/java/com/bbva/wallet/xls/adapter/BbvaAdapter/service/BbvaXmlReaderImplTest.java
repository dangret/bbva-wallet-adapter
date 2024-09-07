package com.bbva.wallet.xls.adapter.BbvaAdapter.service;


import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.impl.BbvaWalletXmlAdapterImpl;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Account;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;
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
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("classpath:application.yaml")
public class BbvaXmlReaderImplTest {

    @InjectMocks
    BbvaWalletXmlAdapterImpl bbvaXmlReader;
    @Mock
    AccountRepository accountRepository;

    File file;

    @Autowired
    ResourceLoader resourceLoader;

    @Test
    public void RetrieveValidDataWhenGettingRow4Cell1() throws IOException {
        givenValidFile();
        givenValidAccounts();
        assertInformationIsValid();
    }

    private void givenValidAccounts() {
        when(accountRepository.findAll()).thenReturn(List.of(
                Account.builder().id("10").cardLastDigits("1234").build(),
        Account.builder().id("11").cardLastDigits("5678").build()));
    }

    private void assertInformationIsValid() {
        List<Record> entries = bbvaXmlReader.importFromCreditCardBbva(file);
        assertEquals("EMBOTELLADORA DEL NAYA", entries.get(0).getNote());
        assertEquals(new BigDecimal("-174.0"), entries.get(0).getAmount());
        assertEquals("65afbc87-0ec6-3926-8eac-fbd10ae95342", entries.get(0).getId());

        assertEquals("SUFACEN MOLOLOA", entries.get(2).getNote());
        assertEquals(new BigDecimal("-280.96"), entries.get(2).getAmount());

        assertEquals("EMBOTELLADORA DEL NAYA", entries.get(3).getNote());
        assertEquals(new BigDecimal("222.0"), entries.get(3).getAmount());
    }

    private void givenValidFile() throws IOException {
        file = resourceLoader.getResource("classpath:data/testData.xlsx").getFile();
    }


}

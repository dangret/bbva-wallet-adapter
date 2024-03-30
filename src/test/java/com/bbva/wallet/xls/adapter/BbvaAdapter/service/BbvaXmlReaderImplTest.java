package com.bbva.wallet.xls.adapter.BbvaAdapter.service;


import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.impl.BbvaXmlReaderImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BbvaXmlReaderImplTest {

    @InjectMocks
    BbvaXmlReaderImpl bbvaXmlReader;

    File file;

    @Autowired
    ResourceLoader resourceLoader;

    @Test
    public void RetrieveValidDataWhenGettingRow4Cell1() throws IOException {
        givenValidFile();
        assertInformationIsValid();
    }

    private void assertInformationIsValid() {
        List<Entry> entries = bbvaXmlReader.read(file);
        assertEquals(1, bbvaXmlReader.read(file).size());
        assertEquals("     Titular *1234", entries.get(0).getDescription());
    }

    private void givenValidFile() throws IOException {
        file = resourceLoader.getResource("classpath:data/testData.xlsx").getFile();
    }


}

package com.bbva.wallet.xls.adapter.BbvaAdapter.service;


import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
import com.bbva.wallet.xls.adapter.BbvaAdapter.mapper.EntryMapper;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.EntryRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.impl.EntryServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class EntryServiceTest {

    @InjectMocks
    EntryServiceImpl entryService;
    @Autowired
    ResourceLoader resourceLoader;
    @Mock
    EntryRepository entryRepository;

    @Mock
    EntryMapper entryMapper;

    List<Entry> entries;

    @Test
    public void saveToDB() throws IOException {
        givenValidEntries();
        whenPersistData();
        verifyDataIsSavedInDB();
    }

    private void verifyDataIsSavedInDB() {
        verify(entryRepository, times(1)).saveAll(anyList());
    }

    private void whenPersistData() {
        entryService.save(entries);
    }

    private void givenValidEntries() throws IOException {
        entries = List.of(Entry.builder()
                        .description("dummy description")
                        .amount(BigDecimal.valueOf(1234f))
                        .date(LocalDate.now())
                        .account("1234")
                .build());
    }


}

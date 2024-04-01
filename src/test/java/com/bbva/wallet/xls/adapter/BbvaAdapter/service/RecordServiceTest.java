package com.bbva.wallet.xls.adapter.BbvaAdapter.service;


import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Account;
import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.AccountEntity;
import com.bbva.wallet.xls.adapter.BbvaAdapter.mapper.RecordMapper;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.AccountRepository;
import com.bbva.wallet.xls.adapter.BbvaAdapter.repository.RecordRepository;
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
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource("classpath:application.yaml")
public class RecordServiceTest {

    @InjectMocks
    EntryServiceImpl entryService;
    @Autowired
    ResourceLoader resourceLoader;
    @Mock
    RecordRepository recordRepository;
    @Mock
    AccountRepository accountRepository;

    @Mock
    RecordMapper recordMapper;

    List<Record> entries;

    @Test
    public void saveToDB() throws IOException {
        givenValidEntries();
        givenValidAccounts();
        whenPersistData();
        verifyDataIsSavedInDB();
    }

    private void givenValidAccounts() {
        AccountEntity account = new AccountEntity();
        account.setId("10");
        account.setCardLastDigits("1234");
        when(accountRepository.findAll()).thenReturn(List.of(account));
    }

    private void verifyDataIsSavedInDB() {
        verify(recordRepository, times(0)).saveAll(anyList());
    }

    private void whenPersistData() {
        entryService.save(entries);
    }

    private void givenValidEntries() throws IOException {
        entries = List.of(Record.builder()
                        .note("dummy description")
                        .amount(BigDecimal.valueOf(1234f))
                        .date(LocalDate.now())
                        .account(Account.builder()
                                .cardLastDigits("1234")
                                .name("dummy account")
                                .build())
                .build());
    }


}

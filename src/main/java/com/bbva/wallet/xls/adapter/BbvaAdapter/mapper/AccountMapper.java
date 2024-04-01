package com.bbva.wallet.xls.adapter.BbvaAdapter.mapper;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Account;
import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.AccountEntity;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.RecordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    Account toAccount(AccountEntity account);
}

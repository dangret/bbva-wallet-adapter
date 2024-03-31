package com.bbva.wallet.xls.adapter.BbvaAdapter.mapper;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.EntryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EntryMapper {
    EntryEntity toEntity(Entry entry);
    List<EntryEntity> toEntities(List<Entry> entries);
    List<Entry> toEntry(List<EntryEntity> entryEntities);
}

package com.bbva.wallet.xls.adapter.BbvaAdapter.entity.generator;

import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.EntryEntity;
import com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class EntryIdGenerator implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return Util.calculateId((EntryEntity) o);
    }
}

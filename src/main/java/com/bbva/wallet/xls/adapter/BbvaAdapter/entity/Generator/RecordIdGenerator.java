package com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Generator;


import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class RecordIdGenerator implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return Util.calculateId((Record) o);
    }
}
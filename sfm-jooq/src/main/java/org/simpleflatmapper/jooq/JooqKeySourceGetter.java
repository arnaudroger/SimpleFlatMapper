package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.simpleflatmapper.map.context.KeySourceGetter;


public class JooqKeySourceGetter implements KeySourceGetter<JooqFieldKey, Record> {
    
    public static final JooqKeySourceGetter INSTANCE = new JooqKeySourceGetter();
    
    private JooqKeySourceGetter() {
    }
    
    @Override
    public Object getValue(JooqFieldKey key, Record source) {
        return source.getValue(key.getIndex());
    }
}

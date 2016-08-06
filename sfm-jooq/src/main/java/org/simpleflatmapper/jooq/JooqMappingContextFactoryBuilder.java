package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.context.KeySourceGetter;

import java.sql.SQLException;

public class JooqMappingContextFactoryBuilder<R extends Record> extends MappingContextFactoryBuilder<R, JooqFieldKey> {
    public JooqMappingContextFactoryBuilder() {
        super(new JooqKeySourceGetter<R>());
    }

    private static class JooqKeySourceGetter<R extends Record> implements KeySourceGetter<JooqFieldKey, R> {
        @Override
        public Object getValue(JooqFieldKey key, R source) throws SQLException {
            return source.getValue(key.getIndex());
        }
    }
}

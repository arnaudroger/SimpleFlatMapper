package org.sfm.datastax.impl;

import com.datastax.driver.core.Row;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.map.context.KeySourceGetter;
import org.sfm.map.context.MappingContextFactoryBuilder;

public class DatastaxMappingContextFactoryBuilder extends MappingContextFactoryBuilder<Row, DatastaxColumnKey> {
    public DatastaxMappingContextFactoryBuilder() {
        super(new JdbcKeySourceGetter());
    }

    private static class JdbcKeySourceGetter implements KeySourceGetter<DatastaxColumnKey, Row> {
        @Override
        public Object getValue(DatastaxColumnKey key, Row source)  {
            return source.getObject(key.getIndex());
        }
    }
}

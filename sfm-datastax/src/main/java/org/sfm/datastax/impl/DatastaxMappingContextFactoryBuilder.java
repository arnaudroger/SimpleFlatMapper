package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.map.context.KeySourceGetter;
import org.sfm.map.context.MappingContextFactoryBuilder;

public class DatastaxMappingContextFactoryBuilder extends MappingContextFactoryBuilder<GettableByIndexData, DatastaxColumnKey> {
    public DatastaxMappingContextFactoryBuilder() {
        super(new JdbcKeySourceGetter());
    }

    private static class JdbcKeySourceGetter implements KeySourceGetter<DatastaxColumnKey, GettableByIndexData> {
        @Override
        public Object getValue(DatastaxColumnKey key, GettableByIndexData source)  {
            return source.getObject(key.getIndex());
        }
    }
}

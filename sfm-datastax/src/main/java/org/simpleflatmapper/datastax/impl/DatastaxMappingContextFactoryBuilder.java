package org.simpleflatmapper.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;

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

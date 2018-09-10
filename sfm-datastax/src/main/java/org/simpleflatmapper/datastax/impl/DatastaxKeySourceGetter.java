package org.simpleflatmapper.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.map.context.KeySourceGetter;

public class DatastaxKeySourceGetter implements KeySourceGetter<DatastaxColumnKey, GettableByIndexData> {
    public static final DatastaxKeySourceGetter INSTANCE = new DatastaxKeySourceGetter();
    private DatastaxKeySourceGetter() {
    }
    @Override
    public Object getValue(DatastaxColumnKey key, GettableByIndexData source)  {
        return source.getObject(key.getIndex());
    }
}

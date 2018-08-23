package org.simpleflatmapper.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;

public class DatastaxMappingContextFactoryBuilder extends MappingContextFactoryBuilder<GettableByIndexData, DatastaxColumnKey> {
    public DatastaxMappingContextFactoryBuilder() {
        super(DatastaxKeySourceGetter.INSTANCE);
    }

}

package org.simpleflatmapper.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;

public class DatastaxMappingContextFactoryBuilder<S extends GettableByIndexData> extends MappingContextFactoryBuilder<S, DatastaxColumnKey> {
    public DatastaxMappingContextFactoryBuilder(boolean ignoreRootKey) {
        super(DatastaxKeySourceGetter.INSTANCE, ignoreRootKey);
    }

}

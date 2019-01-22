package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;

public class JooqMappingContextFactoryBuilder<R extends Record> extends MappingContextFactoryBuilder<R, JooqFieldKey> {
    public JooqMappingContextFactoryBuilder(boolean ignoreRootKey) {
        super(JooqKeySourceGetter.INSTANCE, ignoreRootKey);
    }

}

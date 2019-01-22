package org.simpleflatmapper.querydsl;

import com.mysema.query.Tuple;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;

public class QueryDslMappingContextFactoryBuilder extends MappingContextFactoryBuilder<Tuple, TupleElementKey> {
    public QueryDslMappingContextFactoryBuilder(boolean ignoreRootKey) {
        super(TupleKeySourceGetter.INSTANCE, ignoreRootKey);
    }

}

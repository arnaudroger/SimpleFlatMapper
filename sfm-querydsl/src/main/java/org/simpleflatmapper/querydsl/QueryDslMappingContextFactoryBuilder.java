package org.simpleflatmapper.querydsl;

import com.mysema.query.Tuple;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.context.KeySourceGetter;

import java.sql.SQLException;

public class QueryDslMappingContextFactoryBuilder extends MappingContextFactoryBuilder<Tuple, TupleElementKey> {
    public QueryDslMappingContextFactoryBuilder() {
        super(new TupleKeySourceGetter());
    }

    private static class TupleKeySourceGetter implements KeySourceGetter<TupleElementKey, Tuple> {
        @Override
        public Object getValue(TupleElementKey key, Tuple source) throws SQLException {
            return source.get(key.getExpression());
        }
    }
}

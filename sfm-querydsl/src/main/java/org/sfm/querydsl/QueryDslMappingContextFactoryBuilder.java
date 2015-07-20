package org.sfm.querydsl;

import com.mysema.query.Tuple;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.context.KeySourceGetter;

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

package org.simpleflatmapper.querydsl;

import com.mysema.query.Tuple;
import org.simpleflatmapper.map.context.KeySourceGetter;

import java.sql.SQLException;

public class TupleKeySourceGetter implements KeySourceGetter<TupleElementKey, Tuple> {
    
    public static final TupleKeySourceGetter INSTANCE = new TupleKeySourceGetter();
    
    private TupleKeySourceGetter() {
        
    }
    
    @Override
    public Object getValue(TupleElementKey key, Tuple source) throws SQLException {
        return source.get(key.getExpression());
    }
}

package org.simpleflatmapper.datastax;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.util.ErrorHelper;


public class BoundStatementMapper<T> {
    private final FieldMapper<T, SettableByIndexData> mapper;

    public BoundStatementMapper(FieldMapper<T, SettableByIndexData> mapper) {
        this.mapper = mapper;
    }

    public BoundStatement mapTo(T object, BoundStatement boundStatement) {
        try {
            mapper.mapTo(object, boundStatement, null);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }
        return boundStatement;
    }

    @Override
    public String toString() {
        return "BoundStatementMapper{" +
                "mapper=" + mapper +
                '}';
    }
}

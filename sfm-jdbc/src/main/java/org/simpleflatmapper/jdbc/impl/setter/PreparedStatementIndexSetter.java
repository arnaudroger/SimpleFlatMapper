package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ContextualIndexedSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementIndexSetter<T> extends ContextualIndexedSetter<PreparedStatement, T> {

    /**
     *
     * @param ps the preparedStatement to bind against
     * @param value the value to bind
     * @param columnIndex the index to start binding at
     * @throws SQLException if an error occurs
     */
    @Override
    void set(PreparedStatement ps, T value, int columnIndex, Context context) throws SQLException;
}

package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.reflect.IndexedSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementIndexSetter<T> extends IndexedSetter<PreparedStatement, T> {

    /**
     *
     * @param ps the preparedStatement to bind against
     * @param value the value to bind
     * @param columnIndex the index to start binding at
     * @throws SQLException if an error occurs
     */
    void set(PreparedStatement ps, T value, int columnIndex) throws SQLException;
}

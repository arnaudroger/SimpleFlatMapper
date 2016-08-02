package org.simpleflatmapper.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryBinder<T> {
    /**
     * Bind the value to a new PreparedStatement.
     * @param value the value
     * @return the created PreparedStatement
     * @throws SQLException if an error occurs
     */
    PreparedStatement bind(T value) throws SQLException;

    /**
     * Bind the value to the specified PreparedStatement.
     * If any parameters is a List or an array it will throw an UnsupportedOperationException.
     * @param value the value
     * @param ps the PreparedStatement
     * @throws SQLException if an error occurs
     * @throws UnsupportedOperationException if a parameter is an array or a List
     */
    void bindTo(T value, PreparedStatement ps) throws SQLException;
}

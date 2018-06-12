package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.map.FieldMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryPreparer<T> {

    /**
     * Create a new QueryBinder based on the underlying query.
     * @param connection the connection
     * @return the PreparedStatement
     * @throws SQLException if an sql error occurs
     */
    QueryBinder<T> prepare(Connection connection) throws SQLException;

    /**
     * Will create a PreparedStatement based on the query.
     * If any parameters is a List or an array it will throw an UnsupportedOperationException.
     * @param connection the connection
     * @return the PreparedStatement
     * @throws SQLException if an error occurs
     * @throws UnsupportedOperationException if a parameter is an array or a List
     */
    PreparedStatement prepareStatement(Connection connection) throws SQLException;


    /**
     * Will create a PreparedStatement mapper on the query.
     * If any parameters is a List or an array it will throw an UnsupportedOperationException.
     * @return the mapper
     * @throws UnsupportedOperationException if a parameter is an array or a List
     */
    FieldMapper<T, PreparedStatement> mapper();

    String toRewrittenSqlQuery(T value);
}

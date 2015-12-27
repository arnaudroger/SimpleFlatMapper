package org.sfm.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementMapper<T> {
    PreparedStatement prepare(Connection connection) throws SQLException;

    void bind(PreparedStatement ps, T value) throws SQLException;

    PreparedStatement prepareAndBind(Connection connection, T value) throws SQLException;
}

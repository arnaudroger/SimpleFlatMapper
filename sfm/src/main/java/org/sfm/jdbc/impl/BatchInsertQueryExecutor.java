package org.sfm.jdbc.impl;

import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public interface BatchInsertQueryExecutor<T> {
    void insert(Connection connection, Collection<T> values, RowHandler<PreparedStatement> postExecute) throws SQLException;
}

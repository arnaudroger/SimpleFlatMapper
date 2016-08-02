package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.core.utils.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public interface BatchQueryExecutor<T> {
    void insert(Connection connection, Collection<T> values, RowHandler<PreparedStatement> postExecute) throws SQLException;
}

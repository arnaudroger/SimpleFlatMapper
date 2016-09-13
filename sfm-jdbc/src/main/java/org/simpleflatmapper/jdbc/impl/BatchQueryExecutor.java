package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public interface BatchQueryExecutor<T> {
    void insert(Connection connection, Collection<T> values, CheckedConsumer<PreparedStatement> postExecute) throws SQLException;
}

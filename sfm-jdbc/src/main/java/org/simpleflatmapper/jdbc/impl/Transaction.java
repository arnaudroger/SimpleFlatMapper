package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.util.ErrorHelper;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {
    private final Connection connection;

    public Transaction(Connection connection) {
        this.connection = connection;
    }


    public Connection connection() {
        return connection;
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void handleError(Throwable e) throws SQLException {
        if (!connection.getAutoCommit()) {
            connection.rollback();
        }
        ErrorHelper.rethrow(e);
    }

    public void close() {
        try {
            connection.close();
        } catch (Throwable t) {
            // swallow
        }
    }
}

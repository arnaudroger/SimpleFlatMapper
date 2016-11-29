package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.SQLFunction;
import org.simpleflatmapper.jdbc.TransactionTemplate;
import org.simpleflatmapper.util.ErrorHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceTransactionTemplate implements TransactionTemplate {

    private final DataSource dataSource;

    public DataSourceTransactionTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <R> R doInTransaction(SQLFunction<? super Connection, ? extends R> sqlFunction) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            R r = sqlFunction.apply(connection);
            commit(connection);
            return r;
        } catch (Throwable e) {
            handleError(e, connection);
            return null; // never reached
        } finally {
            close(connection);
        }
    }

    private void commit(Connection connection) throws SQLException {
        if (!connection.getAutoCommit()) {
            connection.commit();
        }
    }

    private void handleError(Throwable e, Connection connection) throws SQLException {
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch(Throwable t) {
            // swallow not to mask original error
        }
        ErrorHelper.rethrow(e);
    }

    private void close(Connection connection) {
        try {
            connection.close();
        } catch (Throwable t) {
            // swallow
        }
    }
}

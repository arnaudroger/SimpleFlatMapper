package org.sfm.jdbc.impl;

import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class MysqlBatchInsertQueryExecutor<T> implements BatchInsertQueryExecutor<T> {

    private final String table;
    private final String[] columns;
    private final String[] generatedKeys;
    private final MultiIndexFieldMapper<T>[] multiIndexFieldMappers;

    public MysqlBatchInsertQueryExecutor(String table, String[] columns, String[] generatedKeys, MultiIndexFieldMapper<T>[] multiIndexFieldMappers) {
        this.table = table;
        this.columns = columns;
        this.generatedKeys = generatedKeys;
        this.multiIndexFieldMappers = multiIndexFieldMappers;
    }

    @Override
    public void insert(Connection connection, Collection<T> values, RowHandler<PreparedStatement> postExecute) throws SQLException {
        PreparedStatement preparedStatement = prepareStatement(connection, values.size());
        try {
            bindTo(preparedStatement, values);
            preparedStatement.executeUpdate();
            postExecute.handle(preparedStatement);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        } finally {
            try {
                preparedStatement.close();
            } catch(SQLException e) {
                // IGNORE
            }
        }
    }

    private PreparedStatement prepareStatement(Connection connection, int size) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(table).append("(");

        for(int j = 0; j < columns.length; j++) {
            if (j > 0) {
                sb.append(", ");
            }
            sb.append(columns[j]);
        }

        sb.append(") VALUES");

        for(int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("(");

            for(int j = 0; j < columns.length; j++) {
                if (j > 0) {
                    sb.append(", ");
                }
                sb.append("?");
            }

            sb.append(")");

        }
        if (generatedKeys.length == 0) {
            return connection.prepareStatement(sb.toString());
        } else {
            return connection.prepareStatement(sb.toString(), generatedKeys);
        }
    }

    private void bindTo(PreparedStatement preparedStatement, Collection<T> values) throws Exception {
        int i = 0;
        for(T value : values) {
            for(int j = 0; j < multiIndexFieldMappers.length; j++ ) {
                multiIndexFieldMappers[j].map(preparedStatement, value, i);
                i++;
            }
        }
    }
}

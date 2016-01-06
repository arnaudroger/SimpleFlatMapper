package org.sfm.jdbc.impl;

import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class MysqlBatchInsertQueryExecutor<T> implements BatchQueryExecutor<T> {

    private final String table;
    private final String[] insertColumns;
    private final String[] updateColumns;
    private final String[] generatedKeys;
    private final MultiIndexFieldMapper<T>[] multiIndexFieldMappers;

    public MysqlBatchInsertQueryExecutor(
            String table,
            String[] insertColumns,
            String[] updateColumns,
            String[] generatedKeys,
            MultiIndexFieldMapper<T>[] multiIndexFieldMappers) {
        this.table = table;
        this.insertColumns = insertColumns;
        this.updateColumns = updateColumns;
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

    private PreparedStatement prepareStatement(Connection connection, int batchSize) throws SQLException {
        StringBuilder sb = createQuery(batchSize);
        if (generatedKeys.length == 0) {
            return connection.prepareStatement(sb.toString());
        } else {
            return connection.prepareStatement(sb.toString(), generatedKeys);
        }
    }

    private StringBuilder createQuery(int size) {
        StringBuilder sb = new StringBuilder();

        insertInto(sb);
        values(size, sb);
        if (updateColumns != null) {
            onDuplicateKeys(sb);
        }

        return sb;
    }

    private void onDuplicateKeys(StringBuilder sb) {
        sb.append(" ON DUPLICATE KEY UPDATE ");
        for(int i = 0; i < updateColumns.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(updateColumns[i])
              .append(" = VALUES(")
              .append(updateColumns[i])
              .append(")");
        }
    }

    private void values(int size, StringBuilder sb) {
        sb.append(" VALUES");
        for(int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("(");

            for(int j = 0; j < insertColumns.length; j++) {
                if (j > 0) {
                    sb.append(", ");
                }
                sb.append("?");
            }

            sb.append(")");

        }
    }

    private void insertInto(StringBuilder sb) {
        sb.append("INSERT INTO ");
        sb.append(table).append("(");

        for(int j = 0; j < insertColumns.length; j++) {
            if (j > 0) {
                sb.append(", ");
            }
            sb.append(insertColumns[j]);
        }

        sb.append(")");
    }

    private void bindTo(PreparedStatement preparedStatement, Collection<T> values) throws Exception {
        int i = 0;
        for(T value : values) {
            for (MultiIndexFieldMapper<T> multiIndexFieldMapper : multiIndexFieldMappers) {
                multiIndexFieldMapper.map(preparedStatement, value, i);
                i++;
            }
        }
    }
}

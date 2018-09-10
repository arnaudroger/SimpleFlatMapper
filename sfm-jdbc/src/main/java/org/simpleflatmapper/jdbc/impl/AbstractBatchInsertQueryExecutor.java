package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public abstract class AbstractBatchInsertQueryExecutor<T> implements BatchQueryExecutor<T> {
    protected final String[] insertColumns;
    protected final String[] insertColumnExpressions;
    protected final String[] updateColumns;
    protected final String[] generatedKeys;
    
    protected final MultiIndexFieldMapper<T>[] multiIndexFieldMappers;
    protected final ContextFactory contextFactory;
    private final CrudMeta meta;

    public AbstractBatchInsertQueryExecutor(CrudMeta meta, String[] insertColumns, String[] insertColumnExpressions, String[] updateColumns, String[] generatedKeys, MultiIndexFieldMapper<T>[] multiIndexFieldMappers, ContextFactory contextFactory) {
        this.meta = meta;
        this.insertColumns = insertColumns;
        this.insertColumnExpressions = insertColumnExpressions;
        this.updateColumns = updateColumns;
        this.generatedKeys = generatedKeys;
        this.multiIndexFieldMappers = multiIndexFieldMappers;
        this.contextFactory = contextFactory;
    }

    @Override
    public void insert(Connection connection, Collection<T> values, CheckedConsumer<PreparedStatement> postExecute) throws SQLException {
        PreparedStatement preparedStatement = prepareStatement(connection, values.size());
        try {
            bindTo(preparedStatement, values);
            preparedStatement.executeUpdate();
            postExecute.accept(preparedStatement);
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

    protected abstract void onDuplicateKeys(StringBuilder sb);

    private void values(int size, StringBuilder sb) {
        sb.append(" VALUES");
        for(int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("(");

            appendValueExpressions(sb);

            sb.append(")");

        }
    }

    private void appendValueExpressions(StringBuilder sb) {
        for(int j = 0; j < insertColumns.length; j++) {
            if (j > 0) {
                sb.append(", ");
            }
            if (insertColumnExpressions == null || insertColumnExpressions[j] == null) {
                sb.append("?");
            } else {
                sb.append(insertColumnExpressions[j]);
            }
        }
    }

    private void insertInto(StringBuilder sb) {
        appendInsertInto(sb);
        meta.appendTableName(sb);
        sb.append("(");

        for(int j = 0; j < insertColumns.length; j++) {
            if (j > 0) {
                sb.append(", ");
            }
            sb.append(insertColumns[j]);
        }

        sb.append(")");
    }

    protected void appendInsertInto(StringBuilder sb) {
        sb.append("INSERT INTO ");
    }

    private void bindTo(PreparedStatement preparedStatement, Collection<T> values) throws Exception {
        int i = 0;
        Context context = contextFactory.newContext();
        for(T value : values) {
            for (MultiIndexFieldMapper<T> multiIndexFieldMapper : multiIndexFieldMappers) {
                multiIndexFieldMapper.map(preparedStatement, value, i, context);
                i++;
            }
        }
    }
}

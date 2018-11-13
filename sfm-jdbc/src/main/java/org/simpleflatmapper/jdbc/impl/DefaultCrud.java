package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.SelectQuery;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.CheckedConsumer;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public final class DefaultCrud<T, K> implements Crud<T,K> {

    protected final QueryPreparer<T> insertQueryPreparer;
    protected final QueryPreparer<T> updateQueryPreparer;
    protected final QueryPreparer<K> selectQueryPreparer;
    protected final QueryPreparer<K> deleteQueryPreparer;
    protected final QueryPreparer<T> upsertQueryPreparer;
    protected final KeyTupleQueryPreparer<K> keyTupleQueryPreparer;
    protected final JdbcMapper<T> selectQueryMapper;
    protected final JdbcMapper<K> keyMapper;
    protected final boolean hasGeneratedKeys;
    protected final SelectQueryWhereFactory<T> selectQueryWhereFactory;
    protected final String selectFromTableWhere;
    protected final String deleteFromTableWhere;

    public DefaultCrud(QueryPreparer<T> insertQueryPreparer,
                       QueryPreparer<T> updateQueryPreparer,
                       QueryPreparer<K> selectQueryPreparer,
                       QueryPreparer<T> upsertQueryPreparer,
                       KeyTupleQueryPreparer<K> keyTupleQueryPreparer,
                       JdbcMapper<T> selectQueryMapper,
                       QueryPreparer<K> deleteQueryPreparer,
                       JdbcMapper<K> keyMapper, CrudMeta meta,
                       boolean hasGeneratedKeys,
                       SelectQueryWhereFactory<T> selectQueryWhereFactory) {
        this.insertQueryPreparer = insertQueryPreparer;
        this.updateQueryPreparer = updateQueryPreparer;
        this.selectQueryPreparer = selectQueryPreparer;
        this.upsertQueryPreparer = upsertQueryPreparer;
        this.keyTupleQueryPreparer = keyTupleQueryPreparer;
        this.deleteQueryPreparer = deleteQueryPreparer;
        this.selectQueryMapper = selectQueryMapper;
        this.keyMapper = keyMapper;
        this.hasGeneratedKeys = hasGeneratedKeys;
        this.selectQueryWhereFactory = selectQueryWhereFactory;
        
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        meta.appendTableName(sb);
        sb.append(" WHERE ");
        selectFromTableWhere = sb.toString();

        sb = new StringBuilder("DELETE FROM ");
        meta.appendTableName(sb);
        sb.append(" WHERE ");
        deleteFromTableWhere = sb.toString();
        
    }

    @Override
    public void create(Connection connection, T value) throws SQLException {
        create(connection, value, null);
    }

    @Override
    public void create(Connection connection, Collection<T> values) throws SQLException {
        create(connection, values, null);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH create(Connection connection, T value, RH keyConsumer) throws SQLException {
        return executeQueryPreparer(connection, value, keyConsumer, insertQueryPreparer);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH create(Connection connection, Collection<T> values, RH keyConsumer) throws SQLException {
        return executeQueryPreparerInBatchMode(connection, values, keyConsumer, insertQueryPreparer);
    }

    @Override
    public T read(Connection connection, K key) throws SQLException {
        PreparedStatement preparedStatement = selectQueryPreparer.prepare(connection).bind(key);
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return selectQueryMapper.map(resultSet);
            }
        } finally {
            safeClose(preparedStatement);
        }
        return null;
    }

    private void safeClose(PreparedStatement preparedStatement) {
        try {
            preparedStatement.close();
        } catch (SQLException e) {}
    }

    @Override
    public <RH extends CheckedConsumer<? super T>> RH read(Connection connection, Collection<K> keys, RH consumer) throws SQLException {
        PreparedStatement preparedStatement = keyTupleQueryPreparer.prepareStatement(selectFromTableWhere, connection, keys.size());
        try {
            keyTupleQueryPreparer.bindTo(keys, preparedStatement, 0);
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                while (resultSet.next()) {
                    consumer.accept(selectQueryMapper.map(resultSet));
                }
            } finally {
                resultSet.close();
            }
            return consumer;
        } catch(Exception e) {
            return ErrorHelper.rethrow(e);
        } finally {
            safeClose(preparedStatement);
        }
    }


    @Override
    public void update(Connection connection, T value) throws SQLException {
        if (updateQueryPreparer == null) return;
        executeQueryPreparer(connection, value, null, updateQueryPreparer);
    }

    @Override
    public void update(Connection connection, Collection<T> values) throws SQLException {
        if (updateQueryPreparer == null) return;
        executeQueryPreparerInBatchMode(connection, values, null, updateQueryPreparer);
    }

    @Override
    public void delete(Connection connection, K key) throws SQLException {
        executeQueryPreparer(connection, key, null, deleteQueryPreparer);
    }

    @Override
    public void delete(Connection connection, Collection<K> keys) throws SQLException {
        PreparedStatement preparedStatement = keyTupleQueryPreparer.prepareStatement(deleteFromTableWhere, connection, keys.size());
        try {
            keyTupleQueryPreparer.bindTo(keys, preparedStatement, 0);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        } finally {
            safeClose(preparedStatement);
        }
    }


    @Override
    public void createOrUpdate(Connection connection, T value) throws SQLException {
        createOrUpdate(connection, value, null);
    }

    @Override
    public void createOrUpdate(Connection connection, Collection<T> values) throws SQLException {
        createOrUpdate(connection, values, null);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(Connection connection, T value, RH keyConsumer) throws SQLException {
        return executeQueryPreparer(connection, value, keyConsumer, upsertQueryPreparer);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(Connection connection, Collection<T> values, RH keyConsumer) throws SQLException {
        return executeQueryPreparerInBatchMode(connection, values, keyConsumer, upsertQueryPreparer);
    }

    @Override
    public <P> SelectQuery<T, P> where(String whereClause, Type paramClass) {
        return selectQueryWhereFactory.where(whereClause, paramClass);
    }

    protected <RH extends CheckedConsumer<? super K>> RH executeQueryPreparerInBatchMode(Connection connection, Collection<T> values, RH keyConsumer, QueryPreparer<T> queryPreparer) throws SQLException {
        PreparedStatement preparedStatement = queryPreparer.prepareStatement(connection);
        try {
            FieldMapper<T, PreparedStatement> mapper = queryPreparer.mapper();

            for (T value : values) {
                mapper.mapTo(value, preparedStatement, null);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            if (hasGeneratedKeys && keyConsumer != null) {
                handleGeneratedKeys(keyConsumer, preparedStatement);
            }
            return keyConsumer;
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        } finally {
            safeClose(preparedStatement);
        }
        return keyConsumer;
    }

    protected <RH extends CheckedConsumer<? super K>, QPT> RH executeQueryPreparer(Connection connection, QPT value, RH keyConsumer, QueryPreparer<QPT> queryPreparer) throws SQLException {
        PreparedStatement preparedStatement = queryPreparer.prepare(connection).bind(value);
        try {
            preparedStatement.executeUpdate();
            if (hasGeneratedKeys && keyConsumer != null) {
                handleGeneratedKeys(keyConsumer, preparedStatement);
            }
            return keyConsumer;
        } finally {
            safeClose(preparedStatement);
        }
    }

    protected void handleGeneratedKeys(CheckedConsumer<? super K> keyConsumer, PreparedStatement preparedStatement) throws SQLException {
        ResultSet keys = preparedStatement.getGeneratedKeys();
        try {
            while (keys.next()) {
                try {
                    keyConsumer.accept(keyMapper.map(keys));
                } catch (Exception e) {
                    ErrorHelper.rethrow(e);
                }
            }
        } finally {
            keys.close();
        }
    }
}


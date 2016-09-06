package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public final class DefaultCrud<T, K> implements Crud<T,K> {

    protected final QueryPreparer<T> insertQueryPreparer;
    protected final QueryPreparer<T> updateQueryPreparer;
    protected final QueryPreparer<K> selectQueryPreparer;
    protected final QueryPreparer<K> deleteQueryPreparer;
    protected final QueryPreparer<T> upsertQueryPreparer;
    protected final KeyTupleQueryPreparer<K> keyTupleQueryPreparer;
    protected final JdbcMapper<T> selectQueryMapper;
    protected final JdbcMapper<K> keyMapper;
    protected final String table;
    protected final boolean hasGeneratedKeys;

    public DefaultCrud(QueryPreparer<T> insertQueryPreparer,
                       QueryPreparer<T> updateQueryPreparer,
                       QueryPreparer<K> selectQueryPreparer,
                       QueryPreparer<T> upsertQueryPreparer,
                       KeyTupleQueryPreparer<K> keyTupleQueryPreparer,
                       JdbcMapper<T> selectQueryMapper,
                       QueryPreparer<K> deleteQueryPreparer,
                       JdbcMapper<K> keyMapper, String table, boolean hasGeneratedKeys) {
        this.insertQueryPreparer = insertQueryPreparer;
        this.updateQueryPreparer = updateQueryPreparer;
        this.selectQueryPreparer = selectQueryPreparer;
        this.upsertQueryPreparer = upsertQueryPreparer;
        this.keyTupleQueryPreparer = keyTupleQueryPreparer;
        this.deleteQueryPreparer = deleteQueryPreparer;
        this.selectQueryMapper = selectQueryMapper;
        this.keyMapper = keyMapper;
        this.table = table;
        this.hasGeneratedKeys = hasGeneratedKeys;
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
    public <RH extends RowHandler<? super K>> RH create(Connection connection, T value, RH keyConsumer) throws SQLException {
        return executeQueryPreparer(connection, value, keyConsumer, insertQueryPreparer);
    }

    @Override
    public <RH extends RowHandler<? super K>> RH create(Connection connection, Collection<T> values, RH keyConsumer) throws SQLException {
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
    public <RH extends RowHandler<? super T>> RH read(Connection connection, Collection<K> keys, RH rowHandler) throws SQLException {
        PreparedStatement preparedStatement = keyTupleQueryPreparer.prepareStatement("SELECT * FROM " + table + " WHERE ", connection, keys.size());
        try {
            keyTupleQueryPreparer.bindTo(keys, preparedStatement, 0);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rowHandler.handle(selectQueryMapper.map(resultSet));
            }
            return rowHandler;
        } catch(Exception e) {
            return ErrorHelper.rethrow(e);
        } finally {
            safeClose(preparedStatement);
        }
    }


    @Override
    public void update(Connection connection, T value) throws SQLException {
        executeQueryPreparer(connection, value, null, updateQueryPreparer);
    }

    @Override
    public void update(Connection connection, Collection<T> values) throws SQLException {
        executeQueryPreparerInBatchMode(connection, values, null, updateQueryPreparer);
    }

    @Override
    public void delete(Connection connection, K key) throws SQLException {
        executeQueryPreparer(connection, key, null, deleteQueryPreparer);
    }

    @Override
    public void delete(Connection connection, List<K> keys) throws SQLException {
        PreparedStatement preparedStatement = keyTupleQueryPreparer.prepareStatement("DELETE FROM " + table + " WHERE ", connection, keys.size());
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
    public <RH extends RowHandler<? super K>> RH createOrUpdate(Connection connection, T value, RH keyConsumer) throws SQLException {
        return executeQueryPreparer(connection, value, keyConsumer, upsertQueryPreparer);
    }

    @Override
    public <RH extends RowHandler<? super K>> RH createOrUpdate(Connection connection, Collection<T> values, RH keyConsumer) throws SQLException {
        return executeQueryPreparerInBatchMode(connection, values, keyConsumer, upsertQueryPreparer);
    }

    protected <RH extends RowHandler<? super K>> RH executeQueryPreparerInBatchMode(Connection connection, Collection<T> values, RH keyConsumer, QueryPreparer<T> queryPreparer) throws SQLException {
        PreparedStatement preparedStatement = queryPreparer.prepareStatement(connection);
        try {
            Mapper<T, PreparedStatement> mapper = queryPreparer.mapper();

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

    protected <RH extends RowHandler<? super K>, QPT> RH executeQueryPreparer(Connection connection, QPT value, RH keyConsumer, QueryPreparer<QPT> queryPreparer) throws SQLException {
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

    protected void handleGeneratedKeys(RowHandler<? super K> keyConsumer, PreparedStatement preparedStatement) throws SQLException {
        ResultSet keys = preparedStatement.getGeneratedKeys();
        try {
            while (keys.next()) {
                try {
                    keyConsumer.handle(keyMapper.map(keys));
                } catch (Exception e) {
                    ErrorHelper.rethrow(e);
                }
            }
        } finally {
            keys.close();
        }
    }
}


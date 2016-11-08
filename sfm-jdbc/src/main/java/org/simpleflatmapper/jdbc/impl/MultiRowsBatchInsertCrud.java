package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.SelectQuery;
import org.simpleflatmapper.util.CheckedConsumer;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public final class MultiRowsBatchInsertCrud<T, K> implements Crud<T, K> {
    private final BatchQueryExecutor<T> batchInsertQueryExecutor;
    private final BatchQueryExecutor<T> batchUpsertQueryExecutor;

    private final DefaultCrud<T, K> delegate;

    public MultiRowsBatchInsertCrud(DefaultCrud<T, K> delegate,
                                    BatchQueryExecutor<T> batchInsertQueryPreparer,
                                    BatchQueryExecutor<T> batchUpsertQueryExecutor) {
        this.delegate = delegate;
        this.batchInsertQueryExecutor = batchInsertQueryPreparer;
        this.batchUpsertQueryExecutor = batchUpsertQueryExecutor;
    }

    @Override
    public void create(Connection connection, T value) throws SQLException {
        delegate.create(connection, value);
    }

    @Override
    public void create(Connection connection, Collection<T> values) throws SQLException {
        create(connection, values, null);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH create(Connection connection, T value, RH keyConsumer) throws SQLException {
        return delegate.create(connection, value, keyConsumer);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH create(Connection connection, Collection<T> values, final RH keyConsumer) throws SQLException {
        batchInsertQueryExecutor.insert(connection, values, new CheckedConsumer<PreparedStatement>() {
            @Override
            public void accept(PreparedStatement preparedStatement) throws Exception {
                if (delegate.hasGeneratedKeys && keyConsumer != null) {
                    delegate.handleGeneratedKeys(keyConsumer, preparedStatement);
                }
            }
        });
        return keyConsumer;
    }

    @Override
    public T read(Connection connection, K key) throws SQLException {
        return delegate.read(connection, key);
    }

    @Override
    public <RH extends CheckedConsumer<? super T>> RH read(Connection connection, Collection<K> keys, RH consumer) throws SQLException {
        return delegate.read(connection, keys, consumer);
    }

    @Override
    public void update(Connection connection, T value) throws SQLException {
        delegate.update(connection, value);
    }

    @Override
    public void update(Connection connection, Collection<T> values) throws SQLException {
        delegate.update(connection, values);
    }

    @Override
    public void delete(Connection connection, K key) throws SQLException {
        delegate.delete(connection, key);
    }

    @Override
    public void delete(Connection connection, Collection<K> keys) throws SQLException {
        delegate.delete(connection, keys);
    }

    @Override
    public void createOrUpdate(Connection connection, T value) throws SQLException {
        delegate.createOrUpdate(connection, value);
    }

    @Override
    public void createOrUpdate(Connection connection, Collection<T> values) throws SQLException {
        createOrUpdate(connection, values, null);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH  createOrUpdate(Connection connection, T value, RH keyConsumer) throws SQLException {
        return delegate.createOrUpdate(connection, value, keyConsumer);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH  createOrUpdate(Connection connection, Collection<T> values, final RH keyConsumer) throws SQLException {
        batchUpsertQueryExecutor.insert(connection, values, new CheckedConsumer<PreparedStatement>() {
            @Override
            public void accept(PreparedStatement preparedStatement) throws Exception {
                if (delegate.hasGeneratedKeys && keyConsumer != null) {
                    delegate.handleGeneratedKeys(keyConsumer, preparedStatement);
                }
            }
        });
        return keyConsumer;
    }

    @Override
    public <P> SelectQuery<T, P> where(String whereClause, Type paramClass) {
        return delegate.where(whereClause, paramClass);
    }
}

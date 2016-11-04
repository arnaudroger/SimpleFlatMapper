package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.CrudDSL;
import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LazyCrud<T, K> implements Crud<T, K> {

    private final CrudDSL<T, K> crudDSL;
    private final AtomicReference<Crud<T, K>> delegate = new AtomicReference<Crud<T, K>>();
    private final String table;

    public LazyCrud(CrudDSL<T, K> crudDSL, String table) {
        this.crudDSL = crudDSL;
        this.table = table;
    }

    private Crud<T, K> getDelegate(Connection connection) throws SQLException {
        Crud<T, K> crud;
        do {
            crud = delegate.get();

            if (crud != null)
                break;

            Crud<T, K> newCrud = instantiateCrud(connection);
            if (delegate.compareAndSet(null, newCrud)) {
                crud = newCrud;
                break;
            }
        } while(true);
        return crud;
    }

    private Crud<T, K> instantiateCrud(Connection connection) throws SQLException {
        if (table == null) {
            return crudDSL.to(connection);
        } else {
            return crudDSL.table(connection, table);
        }
    }

    @Override
    public void create(Connection connection, T value) throws SQLException {
        getDelegate(connection).create(connection, value);
    }

    @Override
    public void create(Connection connection, Collection<T> values) throws SQLException {
        getDelegate(connection).create(connection, values);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH create(Connection connection, T value, RH keyConsumer) throws SQLException {
        return getDelegate(connection).create(connection, value, keyConsumer);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH create(Connection connection, Collection<T> values, RH keyConsumer) throws SQLException {
        return getDelegate(connection).create(connection, values, keyConsumer);
    }

    @Override
    public T read(Connection connection, K key) throws SQLException {
        return getDelegate(connection).read(connection, key);
    }

    @Override
    public <RH extends CheckedConsumer<? super T>> RH read(Connection connection, Collection<K> keys, RH consumer) throws SQLException {
        return getDelegate(connection).read(connection, keys, consumer);
    }

    @Override
    public void update(Connection connection, T value) throws SQLException {
        getDelegate(connection).update(connection, value);
    }

    @Override
    public void update(Connection connection, Collection<T> values) throws SQLException {
        getDelegate(connection).update(connection, values);
    }

    @Override
    public void delete(Connection connection, K key) throws SQLException {
        getDelegate(connection).delete(connection, key);
    }

    @Override
    public void delete(Connection connection, List<K> keys) throws SQLException {
        getDelegate(connection).delete(connection, keys);
    }

    @Override
    public void createOrUpdate(Connection connection, T value) throws SQLException {
        getDelegate(connection).createOrUpdate(connection, value);
    }

    @Override
    public void createOrUpdate(Connection connection, Collection<T> values) throws SQLException {
        getDelegate(connection).createOrUpdate(connection, values);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(Connection connection, T value, RH keyConsumer) throws SQLException {
        return getDelegate(connection).createOrUpdate(connection, value, keyConsumer);
    }

    @Override
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(Connection connection, Collection<T> values, RH keyConsumer) throws SQLException {
        return getDelegate(connection).createOrUpdate(connection, values, keyConsumer);
    }
}

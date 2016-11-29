package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.util.CheckedConsumer;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 *
 * @param <T> the target type
 * @param <K> the key type
 */
public class ConnectedCrud<T, K> {


    private final TransactionTemplate transactionTemplate;
    private final Crud<T, K> delegate;

    public ConnectedCrud(TransactionTemplate transactionTemplate, Crud<T, K> delegate) {
        this.transactionTemplate = transactionTemplate;
        this.delegate = delegate;
    }

    /**
     * insert value into the db through the specified connection.
     *
     * @param value      the value
     * @throws SQLException if an error occurs
     */
    public void create(final T value) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.create(connection, value);
                    return null;
                }
            });
    }

    /**
     * insert values into the db through the specified connection.
     *
     * @param values      the values
     * @throws SQLException if an error occurs
     */
    public void create(final Collection<T> values) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.create(connection, values);
                    return null;
                }
            });
    }

    /**
     * insert value into the db through the specified connection.
     * Callback keyConsumer with the generated key if one was.
     *
     * @param value       the value
     * @param keyConsumer the key consumer
     * @param <RH>        the type of keyConsumer
     * @return the keyConsumer
     * @throws SQLException
     */
    public <RH extends CheckedConsumer<? super K>> RH create(final T value, final RH keyConsumer) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.create(connection, value, keyConsumer);
                    return null;
                }
            });
        return keyConsumer;
    }

    /**
     * insert values into the db through the specified connection.
     * Callback keyConsumer for the generated keys.
     *
     * @param values       the values
     * @param keyConsumer the key consumer
     * @param <RH>        the type of keyConsumer
     * @return the keyConsumer
     * @throws SQLException
     */
    public <RH extends CheckedConsumer<? super K>> RH create(final Collection<T> values, final RH keyConsumer) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.create(connection, values, keyConsumer);
                    return null;
                }
            });
        return keyConsumer;
    }

    /**
     * retrieve the object with the specified key.
     *
     * @param key        the key
     * @return the object or null if not found
     * @throws SQLException if an error occurs
     */
    public T read(final K key) throws SQLException {
        return
            transactionTemplate
                .doInTransaction(new SQLFunction<Connection, T>() {
                    @Override
                    public T apply(Connection connection) throws SQLException {
                        return delegate.read(connection, key);
                    }
                });
    }

    /**
     * retrieve the objects with the specified keys and pass them to the consumer.
     *
     * @param keys       the keys
     * @param consumer the handler that is callback for each row
     * @throws SQLException if an error occurs
     */
    public <RH extends CheckedConsumer<? super T>> RH read(final Collection<K> keys, final RH consumer) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.read(connection, keys, consumer);
                    return null;
                }
            });
        return consumer;
    }

    /**
     * update the object.
     *
     * @param value      the object
     * @throws SQLException if an error occurs
     */
    public void update(final T value) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.update(connection, value);
                    return null;
                }
            });
    }

    /**
     * update the objects.
     *
     * @param values      the objects
     * @throws SQLException if an error occurs
     */
    public void update(final Collection<T> values) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.update(connection, values);
                    return null;
                }
            });
    }

    /**
     * delete the object with the specified key.
     *
     * @param key        the key
     * @throws SQLException if an error occurs
     */
    public void delete(final K key) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.delete(connection, key);
                    return null;
                }
            });
    }

    /**
     * delete the objects with the specified keys.
     *
     * @param keys       the keys
     * @throws SQLException if an error occurs
     */
    public void delete(final Collection<K> keys) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.delete(connection, keys);
                    return null;
                }
            });
    }

    /**
     * UPSERT only supported on Mysql
     * @param value the value
     * @throws SQLException
     * @throws UnsupportedOperationException
     */
    public void createOrUpdate(final T value) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.createOrUpdate(connection, value);
                    return null;
                }
            });
    }

    /**
     * UPSERT only supported on Mysql
     * @param values the values to upsert
     * @throws SQLException
     * @throws UnsupportedOperationException
     */
    public void createOrUpdate(final Collection<T> values) throws SQLException {
        transactionTemplate
            .doInTransaction(new SQLFunction<Connection, Object>() {
                @Override
                public Object apply(Connection connection) throws SQLException {
                    delegate.createOrUpdate(connection, values);
                    return null;
                }
            });
    }

    /**
     * UPSERT only supported on Mysql and Postgres 9.5.
     * Used the callback with caution has Mysql will return an incremented id event for when no insert actually occurred.
     * @param value the value to upsert
     * @param keyConsumer generated key consumer
     * @param <RH> the keyConsumer type
     * @return the keyConsumer
     * @throws SQLException
     */
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(final T value, final RH keyConsumer) throws SQLException {
        transactionTemplate
                .doInTransaction(new SQLFunction<Connection, Object>() {
                    @Override
                    public Object apply(Connection connection) throws SQLException {
                        delegate.createOrUpdate(connection, value, keyConsumer);
                        return null;
                    }
                });
        return keyConsumer;
    }


    /**
     * UPSERT only supported on Mysql and Postgres 9.5.
     * Used the callback with caution has Mysql will return an incremented id event for when no insert actually occurred.
     * @param values the values to insert
     * @param keyConsumer generated key consumer
     * @param <RH> the keyConsumer type
     * @return the keyConsumer
     * @throws SQLException
     */
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(final Collection<T> values, final RH keyConsumer) throws SQLException {
        transactionTemplate
                .doInTransaction(new SQLFunction<Connection, Object>() {
                    @Override
                    public Object apply(Connection connection) throws SQLException {
                        delegate.createOrUpdate(connection, values, keyConsumer);
                        return null;
                    }
                });
        return keyConsumer;
    }


    public Crud<T, K> crud() {
        return delegate;
    }

    public <P> ConnectedSelectQuery<T, P> where(final String whereClause, final Type paramClass) {
        SelectQuery<T, P> selectQuery = delegate.where(whereClause, paramClass);
        return new ConnectedSelectQuery<T, P>(selectQuery, transactionTemplate);
    }

}

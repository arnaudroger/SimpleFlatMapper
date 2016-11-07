package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ErrorHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 *
 * @param <T> the target type
 * @param <K> the key type
 */
public class ConnectedCrud<T, K> {


    private final DataSource dataSource;
    private final Crud<T, K> delegate;

    public ConnectedCrud(DataSource dataSource, Crud<T, K> delegate) {
        this.dataSource = dataSource;
        this.delegate = delegate;
    }

    /**
     * insert value into the db through the specified connection.
     *
     * @param value      the value
     * @throws SQLException if an error occurs
     */
    public void create(T value) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.create(tx.connection(), value);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
    }

    /**
     * insert values into the db through the specified connection.
     *
     * @param values      the values
     * @throws SQLException if an error occurs
     */
    public void create(Collection<T> values) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.create(tx.connection(), values);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
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
    public <RH extends CheckedConsumer<? super K>> RH create(T value, RH keyConsumer) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.create(tx.connection(), value, keyConsumer);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
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
    public <RH extends CheckedConsumer<? super K>> RH create(Collection<T> values, RH keyConsumer) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.create(tx.connection(), values, keyConsumer);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
        return keyConsumer;
    }

    /**
     * retrieve the object with the specified key.
     *
     * @param key        the key
     * @return the object or null if not found
     * @throws SQLException if an error occurs
     */
    public T read(K key) throws SQLException {
        TX tx = openConnection();
        T value = null;
        try {
            value = delegate.read(tx.connection(), key);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
        return value;
    }

    /**
     * retrieve the objects with the specified keys and pass them to the consumer.
     *
     * @param keys       the keys
     * @param consumer the handler that is callback for each row
     * @throws SQLException if an error occurs
     */
    public <RH extends CheckedConsumer<? super T>> RH read(Collection<K> keys, RH consumer) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.read(tx.connection(), keys, consumer);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
        return consumer;
    }

    /**
     * update the object.
     *
     * @param value      the object
     * @throws SQLException if an error occurs
     */
    public void update(T value) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.update(tx.connection(), value);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
    }

    /**
     * update the objects.
     *
     * @param values      the objects
     * @throws SQLException if an error occurs
     */
    public void update(Collection<T> values) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.update(tx.connection(), values);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
    }

    /**
     * delete the object with the specified key.
     *
     * @param key        the key
     * @throws SQLException if an error occurs
     */
    public void delete(K key) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.delete(tx.connection(), key);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
    }

    /**
     * delete the objects with the specified keys.
     *
     * @param keys       the keys
     * @throws SQLException if an error occurs
     */
    public void delete(Collection<K> keys) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.delete(tx.connection(), keys);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
    }

    /**
     * UPSERT only supported on Mysql
     * @param value the value
     * @throws SQLException
     * @throws UnsupportedOperationException
     */
    public void createOrUpdate(T value) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.createOrUpdate(tx.connection(), value);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
    }

    /**
     * UPSERT only supported on Mysql
     * @param values the values to upsert
     * @throws SQLException
     * @throws UnsupportedOperationException
     */
    public void createOrUpdate(Collection<T> values) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.createOrUpdate(tx.connection(), values);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
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
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(T value, RH keyConsumer) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.createOrUpdate(tx.connection(), value, keyConsumer);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
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
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(Collection<T> values, RH keyConsumer) throws SQLException {
        TX tx = openConnection();
        try {
            delegate.createOrUpdate(tx.connection(), values, keyConsumer);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
        return keyConsumer;
    }


    public Crud<T, K> crud() {
        return delegate;
    }


    private TX  openConnection() throws SQLException {
        return TX.from(dataSource);
    }
    private static class TX {
        private final Connection connection;

        private TX(Connection connection) {
            this.connection = connection;
        }

        public static TX from(DataSource dataSource) throws SQLException {
            return new TX(dataSource.getConnection());
        }

        public Connection connection() {
            return connection;
        }

        public void commit() throws SQLException {
            connection.commit();
        }

        public void handleError(Throwable e) throws SQLException {
            connection.rollback();
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
}

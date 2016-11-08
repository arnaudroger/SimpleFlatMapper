package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.jdbc.impl.Transaction;
import org.simpleflatmapper.jdbc.impl.TransactionFactory;
import org.simpleflatmapper.util.CheckedConsumer;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collection;

/**
 *
 * @param <T> the target type
 * @param <K> the key type
 */
public class ConnectedCrud<T, K> {


    private final TransactionFactory transactionFactory;
    private final Crud<T, K> delegate;

    public ConnectedCrud(TransactionFactory transactionFactory, Crud<T, K> delegate) {
        this.transactionFactory = transactionFactory;
        this.delegate = delegate;
    }

    /**
     * insert value into the db through the specified connection.
     *
     * @param value      the value
     * @throws SQLException if an error occurs
     */
    public void create(T value) throws SQLException {
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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
        Transaction tx = newTransaction();
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

    public <P> ConnectedSelectQuery<T, P> where(String whereClause, Type paramClass) {
        SelectQuery<T, P> selectQuery = delegate.where(whereClause, paramClass);
        return new ConnectedSelectQuery<T, P>(selectQuery, transactionFactory);
    }

    private Transaction newTransaction() throws SQLException {
        return transactionFactory.newTransaction();
    }

}

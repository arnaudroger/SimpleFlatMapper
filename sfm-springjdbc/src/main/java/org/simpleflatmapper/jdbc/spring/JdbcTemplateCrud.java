package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.jdbc.*;
import org.simpleflatmapper.util.CheckedConsumer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class JdbcTemplateCrud<T, K> {

    private final JdbcOperations jdbcTemplate;
    private final Crud<T, K> crud;

    public JdbcTemplateCrud(JdbcOperations jdbcOperations, Crud<T, K> crud) {
        this.jdbcTemplate = jdbcOperations;
        this.crud = crud;
    }

    /**
     * insert value into the db through the specified connection.
     *
     * @param value      the value
     * @throws DataAccessException if an error occurs
     */
    public void create(final T value) throws DataAccessException {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                crud.create(connection, value);
                return null;
            }
        });
    }

    /**
     * insert values into the db through the specified connection.
     *
     * @param values      the values
     * @throws DataAccessException if an error occurs
     */
    public void create(final Collection<T> values) throws DataAccessException {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                crud.create(connection, values);
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
     * @throws DataAccessException
     */
    public <RH extends CheckedConsumer<? super K>> RH create(final T value, final RH keyConsumer) throws DataAccessException {
        return
            jdbcTemplate.execute(new ConnectionCallback<RH>() {
                @Override
                public RH doInConnection(Connection connection) throws SQLException, DataAccessException {
                    return crud.create(connection, value, keyConsumer);
                }
            });
    }

    /**
     * insert values into the db through the specified connection.
     * Callback keyConsumer for the generated keys.
     *
     * @param values       the values
     * @param keyConsumer the key consumer
     * @param <RH>        the type of keyConsumer
     * @return the keyConsumer
     * @throws DataAccessException
     */
    public <RH extends CheckedConsumer<? super K>> RH create(final Collection<T> values, final RH keyConsumer) throws DataAccessException {
        return
            jdbcTemplate.execute(new ConnectionCallback<RH>() {
                @Override
                public RH doInConnection(Connection connection) throws SQLException, DataAccessException {
                    return crud.create(connection, values, keyConsumer);
                }
            });
    }

    /**
     * retrieve the object with the specified key.
     *
     * @param key        the key
     * @return the object or null if not found
     * @throws DataAccessException if an error occurs
     */
    public T read(final K key) throws DataAccessException {
        return
            jdbcTemplate.execute(new ConnectionCallback<T>() {
                @Override
                public T doInConnection(Connection connection) throws SQLException, DataAccessException {
                    return crud.read(connection, key);
                }
            });
    }

    /**
     * retrieve the objects with the specified keys and pass them to the consumer.
     *
     * @param keys       the keys
     * @param consumer the handler that is callback for each row
     * @throws DataAccessException if an error occurs
     */
    public <RH extends CheckedConsumer<? super T>> RH read(final Collection<K> keys, final RH consumer) throws DataAccessException {
        return
            jdbcTemplate.execute(new ConnectionCallback<RH>() {
                @Override
                public RH doInConnection(Connection connection) throws SQLException, DataAccessException {
                    return crud.read(connection, keys, consumer);
                }
            });
    }

    /**
     * update the object.
     *
     * @param value      the object
     * @throws DataAccessException if an error occurs
     */
    public void update(final T value) throws DataAccessException {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                crud.update(connection, value);
                return null;
            }
        });
    }

    /**
     * update the objects.
     *
     * @param values      the objects
     * @throws DataAccessException if an error occurs
     */
    public void update(final Collection<T> values) throws DataAccessException {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                crud.update(connection, values);
                return null;
            }
        });
    }

    /**
     * delete the object with the specified key.
     *
     * @param key        the key
     * @throws DataAccessException if an error occurs
     */
    public void delete(final K key) throws DataAccessException {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                crud.delete(connection, key);
                return null;
            }
        });
    }

    /**
     * delete the objects with the specified keys.
     *
     * @param keys       the keys
     * @throws DataAccessException if an error occurs
     */
    public void delete(final List<K> keys) throws DataAccessException {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                crud.delete(connection, keys);
                return null;
            }
        });
    }

    /**
     * UPSERT only supported on Mysql
     * @param value the value
     * @throws DataAccessException
     * @throws UnsupportedOperationException
     */
    public void createOrUpdate(final T value) throws DataAccessException {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                crud.createOrUpdate(connection, value);
                return null;
            }
        });
    }

    /**
     * UPSERT only supported on Mysql
     * @param values the values to upsert
     * @throws DataAccessException
     * @throws UnsupportedOperationException
     */
    public void createOrUpdate(final Collection<T> values) throws DataAccessException {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                crud.createOrUpdate(connection, values);
                return null;
            }
        });
    }

    /**
     * UPSERT only supported on Mysql.
     * Used the callback with caution has Mysql will return an incremented id event for when no insert actually occurred.
     * @param value the value to upsert
     * @param keyConsumer generated key consumer
     * @param <RH> the keyConsumer type
     * @return the keyConsumer
     * @throws DataAccessException
     */
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(final T value, final RH keyConsumer) throws DataAccessException {
        return jdbcTemplate.execute(new ConnectionCallback<RH>() {
            @Override
            public RH doInConnection(Connection connection) throws SQLException, DataAccessException {
                return crud.createOrUpdate(connection, value, keyConsumer);
            }
        });
    }


    /**
     * UPSERT only supported on Mysql.
     * Used the callback with caution has Mysql will return an incremented id event for when no insert actually occurred.
     * @param values the values to insert
     * @param keyConsumer generated key consumer
     * @param <RH> the keyConsumer type
     * @return the keyConsumer
     * @throws DataAccessException
     */
    public <RH extends CheckedConsumer<? super K>> RH createOrUpdate(final Collection<T> values, final RH keyConsumer) throws DataAccessException {
        return jdbcTemplate.execute(new ConnectionCallback<RH>() {
            @Override
            public RH doInConnection(Connection connection) throws SQLException, DataAccessException {
                return crud.createOrUpdate(connection, values, keyConsumer);
            }
        });
    }

    public <P> ConnectedSelectQuery<T, P> where(String whereClause, Type paramClass) {
        SelectQuery<T, P> selectQuery = crud.where(whereClause, paramClass);
        return new ConnectedSelectQuery<T, P>(selectQuery,
                new JdbcTemplateTransactionTemplate(jdbcTemplate));
    }

    private static class JdbcTemplateTransactionTemplate implements TransactionTemplate {

        private final JdbcOperations jdbcOperations;

        private JdbcTemplateTransactionTemplate(JdbcOperations jdbcOperations) {
            this.jdbcOperations = jdbcOperations;
        }

        @Override
        public <R> R doInTransaction(final SQLFunction<? super Connection, ? extends R> sqlFunction) throws DataAccessException {
            return jdbcOperations.execute(new ConnectionCallback<R>() {
                @Override
                public R doInConnection(Connection con) throws SQLException, DataAccessException {
                    return sqlFunction.apply(con);
                }
            });
        }
    }
}

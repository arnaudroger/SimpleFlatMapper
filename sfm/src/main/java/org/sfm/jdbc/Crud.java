package org.sfm.jdbc;

import org.sfm.jdbc.impl.KeyTupleQueryPreparer;
import org.sfm.map.Mapper;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 *
 * @param <T> the target type
 * @param <K> the key type
 */
public class Crud<T, K> {

    private final QueryPreparer<T> insertQueryPreparer;
    private final QueryPreparer<T> updateQueryPreparer;
    private final QueryPreparer<K> selectQueryPreparer;
    private final QueryPreparer<K> deleteQueryPreparer;
    private final KeyTupleQueryPreparer<K> keyTupleQueryPreparer;
    private final JdbcMapper<T> selectQueryMapper;
    private final JdbcMapper<K> keyMapper;
    private final String table;

    public Crud(QueryPreparer<T> insertQueryPreparer,
                QueryPreparer<T> updateQueryPreparer,
                QueryPreparer<K> selectQueryPreparer,
                KeyTupleQueryPreparer<K> keyTupleQueryPreparer, JdbcMapper<T> selectQueryMapper,
                QueryPreparer<K> deleteQueryPreparer, JdbcMapper<K> keyMapper, String table) {
        this.insertQueryPreparer = insertQueryPreparer;
        this.updateQueryPreparer = updateQueryPreparer;
        this.selectQueryPreparer = selectQueryPreparer;
        this.keyTupleQueryPreparer = keyTupleQueryPreparer;
        this.deleteQueryPreparer = deleteQueryPreparer;
        this.selectQueryMapper = selectQueryMapper;
        this.keyMapper = keyMapper;
        this.table = table;
    }

    /**
     * insert value into the db through the specified connection.
     *
     * @param connection the connection
     * @param value      the value
     * @throws SQLException if an error occurs
     */
    public void create(Connection connection, T value) throws SQLException {
        create(connection, value, null);
    }

    public void create(Connection connection, Collection<T> values) throws SQLException {
        create(connection, values, null);
    }

    /**
     * insert value into the db through the specified connection.
     * Callback keyConsumer with the generated key if one was.
     *
     * @param connection  the connection
     * @param value       the value
     * @param keyConsumer the key consumer
     * @param <RH>        the type of keyConsumer
     * @return the keyConsumer
     * @throws SQLException
     */
    public <RH extends RowHandler<? super K>> RH create(Connection connection, T value, RH keyConsumer) throws SQLException {
        PreparedStatement preparedStatement = insertQueryPreparer.prepare(connection).bind(value);
        try {
            preparedStatement.executeUpdate();
            if (keyConsumer != null) {
                handeGeneratedKeys(keyConsumer, preparedStatement);
            }
            return keyConsumer;
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // IGNORE
            }
        }
    }

    public <RH extends RowHandler<? super K>> RH create(Connection connection, Collection<T> values, RH keyConsumer) throws SQLException {

        PreparedStatement preparedStatement = insertQueryPreparer.prepareStatement(connection);
        try {
            Mapper<T, PreparedStatement> mapper = insertQueryPreparer.mapper();

            for (T value : values) {
                mapper.mapTo(value, preparedStatement, null);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            if (keyConsumer != null) {
                handeGeneratedKeys(keyConsumer, preparedStatement);
            }
            return keyConsumer;
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // IGNORE
            }
        }
        return keyConsumer;
    }

    private void handeGeneratedKeys(RowHandler<? super K> keyConsumer, PreparedStatement preparedStatement) throws SQLException {
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


    /**
     * retrieve the object with the specified key.
     *
     * @param connection the connection
     * @param key        the key
     * @return the object or null if not found
     * @throws SQLException if an error occurs
     */
    public T read(Connection connection, K key) throws SQLException {
        PreparedStatement preparedStatement = selectQueryPreparer.prepare(connection).bind(key);
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return selectQueryMapper.map(resultSet);
            }
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // IGNORE
            }
        }
        return null;
    }

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
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // IGNORE
            }
        }
    }


    /**
     * update the object.
     *
     * @param connection the connection
     * @param value      the object
     * @throws SQLException if an error occurs
     */
    public void update(Connection connection, T value) throws SQLException {
        PreparedStatement preparedStatement = updateQueryPreparer.prepare(connection).bind(value);
        try {
            preparedStatement.executeUpdate();
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // IGNORE
            }
        }
    }

    public void update(Connection connection, Collection<T> values) throws SQLException {
        PreparedStatement preparedStatement = updateQueryPreparer.prepareStatement(connection);
        try {
            Mapper<T, PreparedStatement> mapper = updateQueryPreparer.mapper();

            for (T value : values) {
                mapper.mapTo(value, preparedStatement, null);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // IGNORE
            }
        }
    }

    /**
     * delete the object with the specified key.
     *
     * @param connection the connection
     * @param key        the key
     * @throws SQLException if an error occurs
     */
    public void delete(Connection connection, K key) throws SQLException {
        PreparedStatement preparedStatement = deleteQueryPreparer.prepare(connection).bind(key);
        try {
            preparedStatement.executeUpdate();
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // IGNORE
            }
        }
    }

    public void delete(Connection connection, List<K> keys) throws SQLException {
        PreparedStatement preparedStatement = keyTupleQueryPreparer.prepareStatement("DELETE FROM " + table + " WHERE ", connection, keys.size());
        try {
            keyTupleQueryPreparer.bindTo(keys, preparedStatement, 0);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // IGNORE
            }
        }
    }

}


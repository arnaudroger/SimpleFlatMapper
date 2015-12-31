package org.sfm.jdbc;

import org.sfm.jdbc.named.NamedSqlQuery;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class Crud<T, K> {

    private final QueryPreparer<T> insertQueryPreparer;
    private final QueryPreparer<T> updateQueryPreparer;
    private final QueryPreparer<K> selectQueryPreparer;
    private final QueryPreparer<K> deleteQueryPreparer;
    private final JdbcMapper<T> selectQueryMapper;

    public Crud(QueryPreparer<T> insertQueryPreparer,
                QueryPreparer<T> updateQueryPreparer,
                QueryPreparer<K> selectQueryPreparer,
                JdbcMapper<T> selectQueryMapper,
                QueryPreparer<K> deleteQueryPreparer) {
        this.insertQueryPreparer = insertQueryPreparer;
        this.updateQueryPreparer = updateQueryPreparer;
        this.selectQueryPreparer = selectQueryPreparer;
        this.deleteQueryPreparer = deleteQueryPreparer;
        this.selectQueryMapper = selectQueryMapper;
    }

    public void create(Connection connection, T value) throws SQLException {
        PreparedStatement preparedStatement = insertQueryPreparer.prepare(connection).bind(value);
        try {
            preparedStatement.executeUpdate();
        } finally {
            try { preparedStatement.close(); }
            catch (SQLException e) {
                // IGNORE
            }
        }
    }


    public T read(Connection connection, K key) throws SQLException {
        PreparedStatement preparedStatement = selectQueryPreparer.prepare(connection).bind(key);
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return selectQueryMapper.map(resultSet);
            }
            return null;
        } finally {
            try { preparedStatement.close(); }
            catch (SQLException e) {
                // IGNORE
            }
        }
    }

    public void update(Connection connection, T value) throws SQLException {
        PreparedStatement preparedStatement = updateQueryPreparer.prepare(connection).bind(value);
        try {
            preparedStatement.executeUpdate();
        } finally {
            try { preparedStatement.close(); }
            catch (SQLException e) {
                // IGNORE
            }
        }
    }

    public void delete(Connection connection, K key) throws SQLException {
        PreparedStatement preparedStatement = deleteQueryPreparer.prepare(connection).bind(key);
        try {
            preparedStatement.executeUpdate();
        } finally {
            try { preparedStatement.close(); }
            catch (SQLException e) {
                // IGNORE
            }
        }
    }
}

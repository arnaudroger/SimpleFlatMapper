package org.sfm.jdbc;

import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;


public interface Crud<T, K> {
    void create(Connection connection, T value) throws SQLException;

    void create(Connection connection, Collection<T> values) throws SQLException;

    <RH extends RowHandler<? super K>> RH create(Connection connection, T value, RH keyConsumer) throws SQLException;

    <RH extends RowHandler<? super K>> RH create(Connection connection, Collection<T> values, RH keyConsumer) throws SQLException;

    T read(Connection connection, K key) throws SQLException;

    <RH extends RowHandler<? super T>> RH read(Connection connection, Collection<K> keys, RH rowHandler) throws SQLException;

    void update(Connection connection, T value) throws SQLException;

    void update(Connection connection, Collection<T> values) throws SQLException;

    void delete(Connection connection, K key) throws SQLException;

    void delete(Connection connection, List<K> keys) throws SQLException;

    void createOrUpdate(Connection connection, T value) throws SQLException;

    void createOrUpdate(Connection connection, Collection<T> values) throws SQLException;
}

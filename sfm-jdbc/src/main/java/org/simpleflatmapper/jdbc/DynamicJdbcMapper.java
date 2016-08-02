package org.simpleflatmapper.jdbc;


import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public interface DynamicJdbcMapper<T> extends JdbcMapper<T> {
    JdbcMapper<T> getMapper(ResultSetMetaData metaData) throws SQLException;
}

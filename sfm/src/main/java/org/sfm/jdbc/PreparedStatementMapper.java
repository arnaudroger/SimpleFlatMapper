package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementMapper<T> {
    void map(T t, PreparedStatement ps) throws SQLException;
}

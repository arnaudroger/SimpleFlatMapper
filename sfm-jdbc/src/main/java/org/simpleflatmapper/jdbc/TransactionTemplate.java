package org.simpleflatmapper.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionTemplate {
    <R> R doInTransaction(SQLFunction<? super Connection, ? extends R> sqlFunction) throws SQLException;

}

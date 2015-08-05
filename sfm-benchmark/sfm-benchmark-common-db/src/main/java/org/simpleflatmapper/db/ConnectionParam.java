package org.simpleflatmapper.db;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@State(Scope.Benchmark)
public class ConnectionParam {
    @Param(value="HSQLDB")
    public DbTarget db;

    public DataSource dataSource;

    public Connection connection;

    @Setup
    public void init() throws SQLException, NamingException {
        dataSource = ConnectionHelper.getDataSource(db);

        if (db != DbTarget.MOCK) {
            Connection conn = dataSource.getConnection();
            try {
                ConnectionHelper.createTableAndInsertData(conn);
            } finally {
                conn.close();
            }
        }
        connection = dataSource.getConnection();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}

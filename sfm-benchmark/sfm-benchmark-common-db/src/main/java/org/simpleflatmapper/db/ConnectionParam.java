package org.simpleflatmapper.db;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@State(Scope.Benchmark)
public class ConnectionParam {
    @Param(value="H2")
    public DbTarget db;

    public DataSource dataSource;

    public Connection connection;

    @Setup
    public void init() throws SQLException, NamingException {
        dataSource = ConnectionHelper.getDataSource(db);

        if (db != DbTarget.MOCK) {
            Connection conn = dataSource.getConnection();
            try {
                ConnectionHelper.createTableAndInsertData(conn, ConnectionHelper.Table.SMALL);
                ConnectionHelper.createTableAndInsertData(conn, ConnectionHelper.Table.BIG);
            } finally {
                conn.close();
            }
        }
        connection = dataSource.getConnection();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void executeStatement(String statement, ResultSetHandler handler, Object... params) throws Exception {
        PreparedStatement prepareStatement = connection.prepareStatement(statement);
        try {
            setParams(prepareStatement, params);
            ResultSet rs = prepareStatement.executeQuery();
            try {
                handler.handle(rs);
            }finally {
                rs.close();
            }
        } finally {
            prepareStatement.close();
        }
    }

    public void setParams(PreparedStatement prepareStatement, Object[] params) throws SQLException {
        if (params != null) {
            for(int i = 0; i < params.length; i++) {
                prepareStatement.setObject(i + 1, params[i]);
            }
        }
    }
}

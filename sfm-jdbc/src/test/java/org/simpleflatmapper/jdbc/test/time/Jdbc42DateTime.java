package org.simpleflatmapper.jdbc.test.time;

import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class Jdbc42DateTime {

    public static void main(String[] args) throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);

        final DatabaseMetaData metaData = connection.getMetaData();

        final String driverVersion = metaData.getDriverVersion();
        System.out.println("driverVersion = " + driverVersion);
        System.out.println("jdbcVersion = " + metaData.getJDBCMajorVersion()
        + "." + metaData.getJDBCMinorVersion());

        final String timeDateFunctions = metaData.getTimeDateFunctions();
        System.out.println("timeDateFunctions = " + timeDateFunctions);

        ResultSet rs = metaData.getTypeInfo();

        while(rs.next()) {
            for(int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                System.out.print(rs.getMetaData().getColumnName(i + 1) + " = " + rs.getObject(i + 1));

                System.out.print(", ");
            }
            System.out.println("");
        }

        final Statement statement = connection.createStatement();

        final ResultSet resultSet = statement.executeQuery("SELECT current_timestamp");

        while (resultSet.next()) {
            final String columnTypeName = resultSet.getMetaData().getColumnTypeName(1);
            System.out.println("columnTypeName = " + columnTypeName);
            final Timestamp timestamp = resultSet.getTimestamp(1);
            System.out.println("resultSet = " + timestamp);
            final Object object = resultSet.getObject(1, OffsetDateTime.class);
            System.out.println("resultSet = " + object + "/" + object.getClass());
        }

    }
}

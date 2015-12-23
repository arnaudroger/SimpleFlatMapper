package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static org.junit.Assert.*;

public class PreparedStatementMapperTest {

    @Test
    public void testInsertWithPreparedStatementMapper() throws SQLException {
        DbObject dbObject = DbObject.newInstance();

        NamedSqlQuery insertQuery = NamedSqlQuery.parse("INSERT INTO test_db_object(id, name, email, creation_time, type_ordinal, type_name) values(?, ?, ?, ?, ?, ?) ");
        NamedSqlQuery selectQuery = NamedSqlQuery.parse("select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id = ? ");

        JdbcMapperFactory jdbcMapperFactory = JdbcMapperFactory
                .newInstance()
                .addColumnProperty("type_ordinal", SqlTypeColumnProperty.of(Types.NUMERIC));
        PreparedStatementMapper<DbObject> insertPreparedStatementMapper =
                jdbcMapperFactory
                        .from(DbObject.class).to(insertQuery);

        PreparedStatementMapper<DbObject> selectPreparedStatementMapper =
                jdbcMapperFactory.from(DbObject.class).to(selectQuery);


        Connection connection = DbHelper.objectDb();
        try {
            PreparedStatement ps = insertPreparedStatementMapper.prepareAndBind(connection, dbObject);
            try {
                ps.execute();
            } finally {
                ps.close();
            }


            ps = selectPreparedStatementMapper.prepareAndBind(connection, dbObject);
            try {
                ps.setLong(1, dbObject.getId());

                ResultSet resultSet = ps.executeQuery();

                assertTrue(resultSet.next());
                assertEquals(dbObject, JdbcMapperFactory.newInstance().newMapper(DbObject.class).map(resultSet));
            } finally {
                ps.close();
            }


        } finally {
            connection.close();
        }


    }
}
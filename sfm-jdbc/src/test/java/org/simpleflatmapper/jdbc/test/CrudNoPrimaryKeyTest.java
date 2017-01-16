package org.simpleflatmapper.jdbc.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CrudNoPrimaryKeyTest {


    Connection connection;
    @Before
    public void setUp() throws SQLException {
        connection = DbHelper.objectDb();

        Statement statement = connection.createStatement();

        try {
            statement.execute("DROP TABLE IF EXISTS CRUDNOKEY");
            statement.execute("CREATE TABLE CRUDNOKEY ( id bigint, name varchar(255) )");
            statement.execute("INSERT INTO CRUDNOKEY VALUES ( 1, 'name' )");
        } finally {
            statement.close();
        }
    }
    @After
    public void tearDown() throws SQLException {
        connection.close();
        connection = null;
    }

    @Test
    public void testFailOnNoPrimaryKey() throws SQLException {

        try {
            JdbcMapperFactory.newInstance().crud(MyObject.class, long.class).table(connection, "CRUDNOKEY");
            fail();
        } catch (IllegalArgumentException e) {
            System.out.println("e = " + e);
        }
    }
    @Test
    public void testWithManualKey() throws SQLException {
        Crud<MyObject, Long> table = JdbcMapperFactory
                .newInstance()
                .addKeys("id")
                .<MyObject, Long>crud(MyObject.class, Long.class)
                .table(connection, "CRUDNOKEY");

        assertEquals(new MyObject(1l, "name"), table.read(connection, 1l));
    }



    public static class MyObject {
        public long id;
        public String name;

        public MyObject(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public MyObject() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyObject myObject = (MyObject) o;

            if (id != myObject.id) return false;
            return name != null ? name.equals(myObject.name) : myObject.name == null;

        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}

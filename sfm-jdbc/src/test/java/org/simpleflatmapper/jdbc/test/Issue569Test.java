package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Issue569Test {
    
    
    public static class MyPojo {
        private final long id;
        private final String name;
        private final int nb;
        public MyPojo(long id, String name, int nb) {
            this.id = id;
            this.name = name;
            this.nb = nb;
        }

        public int getNb() {
            return nb;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyPojo myPojo = (MyPojo) o;

            if (id != myPojo.id) return false;
            return name != null ? name.equals(myPojo.name) : myPojo.name == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }

    @Test
    public void testMysqlView() throws SQLException {

        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);
        if (connection == null) { System.err.println("Db " + DbHelper.TargetDB.MYSQL + " not available"); return; }

        try {
            Statement st = connection.createStatement();
            
            st.executeUpdate("CREATE TABLE IF NOT EXISTS issue569( id bigint primary key, name varchar(256), version int ) ");
            try {
                st.executeUpdate("DROP VIEW issue569_v");
            } catch (Exception e) {}
            st.executeUpdate("CREATE OR REPLACE  VIEW issue569_v(id, name, nb) as SELECT id, name, count(*) as nb FROM issue569 group by id, name");
            st.executeUpdate("TRUNCATE issue569");
            st.executeUpdate("INSERT INTO issue569 VALUES(1, 'v1', 1), (2, 'v2', 2)");
            
            
            Crud<CrudTest.OnlyKey, Long> objectCrud =
                    JdbcMapperFactory.newInstance()
                            .addColumnProperty("id", KeyProperty.DEFAULT)
                            .<CrudTest.OnlyKey, Long>crud(MyPojo.class, Long.class).table("issue569_v");

            // read
            assertEquals(new MyPojo(1, "v1", 1), objectCrud.read(connection, 1l));
            assertEquals(new MyPojo(2, "v2", 1), objectCrud.read(connection, 2l));

        } finally {
            connection.close();
        }
    }
}

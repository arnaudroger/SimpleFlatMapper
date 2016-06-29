package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class Issue318Test {

    //IFJAVA8_START
    @Test
    public void testLocalDateTimeFromTimestamp() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);

        try {
            Statement statement = connection.createStatement();
            try {
                statement.execute("DROP TABLE IF EXISTS ISSUE318");
                statement.execute("CREATE TABLE ISSUE318 (id integer primary key, t timestamp );");
            } finally {
                statement.close();
            }

            Crud<Issue318, Long> issue318 = JdbcMapperFactory.newInstance()
                    .crud(Issue318.class, Long.class)
                    .table(connection, "ISSUE318");

            Issue318 value = new Issue318(1l, LocalDateTime.now());
            issue318.create(connection, value);

            assertEquals(value, issue318.read(connection, 1l));

            JdbcMapper<Issue318> mapper =
                    JdbcMapperFactory.newInstance().newBuilder(Issue318.class).addMapping("id").addMapping("t").mapper();

            statement = connection.createStatement();
            try {
                assertEquals(value, mapper.iterator(statement.executeQuery("SELECT * FROM ISSUE318")).next());
            } finally {
                statement.close();
            }

        } finally {
            connection.close();
        }


    }

    public static class Issue318 {
        private final Long id;
        private final LocalDateTime t;

        public Issue318(Long id, LocalDateTime t) {
            this.id = id;
            this.t = t;
        }

        public LocalDateTime getT() {
            return t;
        }


        public Long getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Issue318 issue318 = (Issue318) o;

            if (id != null ? !id.equals(issue318.id) : issue318.id != null) return false;
            return t != null ? t.equals(issue318.t) : issue318.t == null;

        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (t != null ? t.hashCode() : 0);
            return result;
        }
    }
    //IFJAVA8_END
}

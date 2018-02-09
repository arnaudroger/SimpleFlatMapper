package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.TypeReference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

//IFJAVA8_START
import java.time.Instant;
import java.util.Optional;
//IFJAVA8_END

import java.util.UUID;


public class Issue486Test {

    @Test
    public void noTestJava7() {
        
    }
    //IFJAVA8_START
    @Test
    public void testIssue() throws SQLException {

        JdbcMapperBuilder<Issue486> builder = JdbcMapperFactory.newInstance().newBuilder(new TypeReference<Issue486>() {
        }.getType());
        
        JdbcMapper<Issue486> mapper = builder.addMapping("t", 1, Types.TIMESTAMP).mapper();


        Connection dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        
        if (dbConnection == null) return;

        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT CURRENT_TIMESTAMP");
            rs.next();
            Issue486 issue486 = mapper.map(rs);
            System.out.println("issue486 = " + issue486.t.get());
        } finally {
            dbConnection.close();
        }
    }

    @Test
    public void testMIssue() throws SQLException {

        JdbcMapperBuilder<NIssue486> builder = JdbcMapperFactory.newInstance().newBuilder(new TypeReference<NIssue486>() {
        }.getType());

        JdbcMapper<NIssue486> mapper = builder.addMapping("t", 1, Types.TIMESTAMP).mapper();


        Connection dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);

        if (dbConnection == null) return;

        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT CURRENT_TIMESTAMP");
            rs.next();
            NIssue486 issue486 = mapper.map(rs);
            System.out.println("issue486 = " + issue486.t);
        } finally {
            dbConnection.close();
        }
    }

    public static class Issue486 {
        private Optional<Instant> t;
        private UUID id;

        public Optional<Instant> getT() {
            return t;
        }

        public void setT(Optional<Instant> t) {
            this.t = t;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }


    public static class NIssue486 {
        private Instant t;
        private UUID id;

        public Instant getT() {
            return t;
        }

        public void setT(Instant t) {
            this.t = t;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }
    //IFJAVA8_END


}

package org.simpleflatmapper.jooq.test;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jooq.SfmRecordMapperProviderFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

//IFJAVA8_START
import java.sql.Statement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
//IFJAVA8_END


public class Issue587Test {

    //IFJAVA8_START
    @Test
    public void testFetchResultSet() throws SQLException {
        Connection conn = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (conn == null) return;
        try {


            Statement statement = conn.createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS issue_587(id int, t timestamptz)");
            statement.executeUpdate("TRUNCATE issue_587");
            statement.executeUpdate("insert into issue_587 values (1, timestamp with time zone '2018-11-15 11:45:11.00000+01:00')");

            DSLContext dsl = DSL
                    .using(new DefaultConfiguration().set(conn)
                            .set(SQLDialect.POSTGRES)
                            .set(SfmRecordMapperProviderFactory.newInstance().ignorePropertyNotFound().newProvider()));

            DynamicJdbcMapper<Issue587> mapper = JdbcMapperFactory.newInstance().newMapper(Issue587.class);
            OffsetDateTime date = OffsetDateTime.parse("2018-11-15T11:45:11+01:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            OffsetDateTime expected = date.withOffsetSameInstant(ZoneOffset.UTC);

            SelectWhereStep<Record> select = dsl.selectFrom("issue_587");


            try (ResultSet rs = select.fetchResultSet()) {
                ResultSetMetaData metaData = rs.getMetaData();
                System.out.println("metaData = " + metaData.getColumnType(2));
                System.out.println("metaData = " + metaData.getColumnClassName(2));
                System.out.println("metaData = " + metaData.getColumnTypeName(2));

                while (rs.next()) {
                    System.out.println("rs.getObject(2) = " + rs.getObject(2));
                }
            }
            

            try (ResultSet rs = select.fetchResultSet()) {
                Issue587[] issue587s = mapper.stream(rs).toArray(Issue587[]::new);
                System.out.println("issue587s = " + Arrays.toString(issue587s));

                assertEquals(1, issue587s.length);

                assertEquals(1, issue587s[0].id);
                assertEquals(expected, issue587s[0].t);
            }

        } finally {
            conn.close();
        }
        

    }

    @Test
    public void testFetchIntoResultSet() throws SQLException {
        Connection conn = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (conn == null) return;
        try  {


            Statement statement = conn.createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS issue_587(id int, t timestamptz)");
            statement.executeUpdate("TRUNCATE issue_587");
            statement.executeUpdate("insert into issue_587 values (1, timestamp with time zone '2018-11-15 11:45:11.00000+01:00')");

            DSLContext dsl = DSL
                    .using(new DefaultConfiguration().set(conn)
                            .set(SQLDialect.POSTGRES)
                            .set(SfmRecordMapperProviderFactory.newInstance().ignorePropertyNotFound().newProvider()));

            DynamicJdbcMapper<Issue587> mapper = JdbcMapperFactory.newInstance().newMapper(Issue587.class);
            OffsetDateTime date = OffsetDateTime.parse("2018-11-15T11:45:11+01:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            OffsetDateTime expected = date.withOffsetSameInstant(ZoneOffset.UTC);

            SelectWhereStep<Record> select = dsl.selectFrom("issue_587");


            try (ResultSet rs = select.fetch().intoResultSet()) {
                ResultSetMetaData metaData = rs.getMetaData();
                System.out.println("metaData = " + metaData.getColumnType(2));
                System.out.println("metaData = " + metaData.getColumnClassName(2));
                System.out.println("metaData = " + metaData.getColumnTypeName(2));
                while (rs.next()) {
                    System.out.println("rs.getObject(2) = " + rs.getObject(2));
                }
            }

            try (ResultSet rs = select.fetch().intoResultSet()) {
                Issue587[] issue587s = mapper.stream(rs).toArray(Issue587[]::new);
                System.out.println("issue587s = " + Arrays.toString(issue587s));

                assertEquals(1, issue587s.length);

                assertEquals(1, issue587s[0].id);
                assertEquals(expected, issue587s[0].t);
            }

        } finally {
            conn.close();
        }


    }

    public static class Issue587 {
        public final OffsetDateTime t;
        public final int id;

        public Issue587(OffsetDateTime t, int id) {
            this.t = t;
            this.id = id;
        }

        @Override
        public String toString() {
            return "Issue587{" +
                    "t=" + t +
                    ", id=" + id +
                    '}';
        }
    }
    //IFJAVA8_END


}

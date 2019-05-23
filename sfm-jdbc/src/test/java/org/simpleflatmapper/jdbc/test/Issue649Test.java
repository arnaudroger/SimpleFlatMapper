package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.property.IndexedSetterProperty;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;

import static org.junit.Assert.assertEquals;

public class Issue649Test {



    //IFJAVA8_START
    @Test
    public void testInetAddress() throws SQLException, UnknownHostException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();

            s.execute("DROP TABLE IF EXISTS issue_649;");
            s.execute("CREATE TABLE issue_649(a bigserial primary key,b inet);");

            Crud<FooInet, Long> crud =
                    JdbcMapperFactory
                        .newInstance()
                            .addColumnProperty("b", IndexedSetterProperty.<InetAddress>of((ps, i, t) -> ps.setObject(i, t.getHostAddress(), Types.OTHER)))
                            .crud(FooInet.class, long.class)
                            .table(c, "issue_649");

            InetAddress inetAddress = InetAddress.getByName("192.168.0.1");
            crud.create(c, new FooInet(1l, inetAddress));

            FooInet foo = crud.read(c, 1l);

            assertEquals(1l, foo.a);
            assertEquals(inetAddress, foo.b);

        } finally {
            c.close();
        }
    }


    public static class FooInet {
        public final long a;
        public final InetAddress b;

        public FooInet(long a, InetAddress b) {
            this.a = a;
            this.b = b;
        }
    }

    @Test
    public void testString() throws SQLException, UnknownHostException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();

            s.execute("DROP TABLE IF EXISTS issue_649;");
            s.execute("CREATE TABLE issue_649(a bigserial primary key,b inet);");

            Crud<FooString, Long> crud =
            JdbcMapperFactory
                    .newInstance()
                    .addColumnProperty("b", new IndexedSetterProperty((ps,  t, i) -> ps.setObject(i, t, Types.OTHER)))
                    .crud(FooString.class, long.class)
                    .table(c, "issue_649");

            String inetAddress = "192.168.0.1";
            crud.create(c, new FooString(1l, inetAddress));

            FooString foo = crud.read(c, 1l);

            assertEquals(1l, foo.a);
            assertEquals(inetAddress, foo.b);

        } finally {
            c.close();
        }
    }


    public static class FooString {
        public final long a;
        public final String b;

        public FooString(long a, String b) {
            this.a = a;
            this.b = b;
        }
    }
    //IFJAVA8_END

}

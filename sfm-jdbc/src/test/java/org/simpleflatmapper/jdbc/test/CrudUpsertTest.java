package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.ConnectedCrud;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.jdbc.impl.ColumnMeta;
import org.simpleflatmapper.jdbc.impl.CrudFactory;
import org.simpleflatmapper.jdbc.impl.CrudMeta;
import org.simpleflatmapper.jdbc.impl.DatabaseMeta;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CrudUpsertTest {

    Crud<DbObject, Long> postGresqlCrud;

    {
        try {
            postGresqlCrud =
                    CrudFactory.<DbObject, Long>newInstance(
                    ReflectionService.newInstance().getClassMeta(DbObject.class),
                    ReflectionService.newInstance().getClassMeta(Long.class),
                    new CrudMeta(new DatabaseMeta("PostgreSQL", 9, 5), "public", "TEST",
                            new ColumnMeta[]{
                                    new ColumnMeta("id", Types.INTEGER, true, null),
                                    new ColumnMeta("name", Types.VARCHAR, false, null)
                            }),
                    JdbcMapperFactory.newInstance());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testUpsertDefaultCrud() throws SQLException {

        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(connection.prepareStatement(
                "INSERT INTO \"public\".\"TEST\"(\"id\", \"name\") " +
                        "VALUES(?, ?) " +
                        "ON CONFLICT (\"id\") " +
                        "DO UPDATE " +
                        "SET \"name\" = EXCLUDED.\"name\"", new String[0])).thenReturn(ps);

        DbObject o = DbObject.newInstance();
        postGresqlCrud.createOrUpdate(connection, o);

        verify(ps).setLong(1, o.getId());
        verify(ps).setString(2, o.getName());
        verify(ps).executeUpdate();
    }

    @Test
    public void testUpsertDefaultCrudBatch() throws SQLException {

        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(connection.prepareStatement(
                "INSERT INTO \"public\".\"TEST\"(id, name) " +
                        "VALUES(?, ?), (?, ?) " +
                        "ON CONFLICT (id) " +
                        "DO UPDATE " +
                        "SET name = EXCLUDED.name")).thenReturn(ps);

        List<DbObject> objects = Arrays.asList(DbObject.newInstance(), DbObject.newInstance());
        postGresqlCrud.createOrUpdate(connection, objects);

        verify(ps).setLong(1, objects.get(0).getId());
        verify(ps).setString(2, objects.get(0).getName());
        verify(ps).setLong(3, objects.get(1).getId());
        verify(ps).setString(4, objects.get(1).getName());

        verify(ps).executeUpdate();
    }

    @Test
    public void testUpsertHsqlBatch() throws Exception {
        testDefaultCrudCreateOrUpdate(DbHelper.getDbConnection(DbHelper.TargetDB.HSQLDB), JdbcMapperFactory.newInstance().crud(DbObject.class, Long.class).table(DbHelper.getDbConnection(DbHelper.TargetDB.HSQLDB), "TEST_DB_OBJECT"));
        testDefaultCrudCreateOrUpdate(DbHelper.getDbConnection(DbHelper.TargetDB.HSQLDB), JdbcMapperFactory.newInstance().crud(DbObject.class, Long.class).table("TEST_DB_OBJECT"));
        testConnectedCrudCreateOrUpdate(JdbcMapperFactory.newInstance().crud(DbObject.class, Long.class).table(DbHelper.getHsqlDataSource(), "TEST_DB_OBJECT"));

    }

    private void testDefaultCrudCreateOrUpdate(Connection connection, Crud<DbObject, Long> crud) throws SQLException {
        List<DbObject> objects = Arrays.asList(DbObject.newInstance(), DbObject.newInstance());

        try {
            crud.createOrUpdate(connection, objects);
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }

        try {
            crud.createOrUpdate(connection, DbObject.newInstance());
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }

        try {
            crud.createOrUpdate(connection, objects, new CheckedConsumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {

                }
            });
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }

        try {
            crud.createOrUpdate(connection, DbObject.newInstance(), new CheckedConsumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {

                }
            });
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }


    private void testConnectedCrudCreateOrUpdate(ConnectedCrud<DbObject, Long> crud) throws SQLException {
        List<DbObject> objects = Arrays.asList(DbObject.newInstance(), DbObject.newInstance());

        try {
            crud.createOrUpdate(objects);
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }

        try {
            crud.createOrUpdate(DbObject.newInstance());
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }

        try {
            crud.createOrUpdate(objects, new CheckedConsumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {

                }
            });
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }

        try {
            crud.createOrUpdate(DbObject.newInstance(), new CheckedConsumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {

                }
            });
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }
}

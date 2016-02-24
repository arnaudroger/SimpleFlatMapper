package org.sfm.jdbc;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.impl.ColumnMeta;
import org.sfm.jdbc.impl.CrudFactory;
import org.sfm.jdbc.impl.CrudMeta;
import org.sfm.jdbc.impl.DatabaseMeta;
import org.sfm.reflect.ReflectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CrudUpsertTest {

    Crud<DbObject, Long> objectCrud;

    {
        try {
            objectCrud =
                    CrudFactory.newInstance(
                    ReflectionService.newInstance().getClassMeta(DbObject.class),
                    ReflectionService.newInstance().getClassMeta(Long.class),
                    new CrudMeta(new DatabaseMeta("PostgreSQL", 5, 5), "TEST",
                            new ColumnMeta[]{
                                    new ColumnMeta("id", Types.INTEGER, true, false),
                                    new ColumnMeta("name", Types.VARCHAR, false, false)
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
                "INSERT INTO TEST(id, name) " +
                        "VALUES(?, ?) " +
                        "ON CONFLICT (id) " +
                        "DO UPDATE " +
                        "SET name = EXCLUDED.name", new String[0])).thenReturn(ps);

        DbObject o = DbObject.newInstance();
        objectCrud.createOrUpdate(connection, o);

        verify(ps).setLong(1, o.getId());
        verify(ps).setString(2, o.getName());
        verify(ps).executeUpdate();
    }

    @Test
    public void testUpsertDefaultCrudBatch() throws SQLException {

        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(connection.prepareStatement(
                "INSERT INTO TEST(id, name) " +
                        "VALUES(?, ?), (?, ?) " +
                        "ON CONFLICT (id) " +
                        "DO UPDATE " +
                        "SET name = EXCLUDED.name")).thenReturn(ps);

        List<DbObject> objects = Arrays.asList(DbObject.newInstance(), DbObject.newInstance());
        objectCrud.createOrUpdate(connection, objects);

        verify(ps).setLong(1, objects.get(0).getId());
        verify(ps).setString(2, objects.get(0).getName());
        verify(ps).setLong(3, objects.get(1).getId());
        verify(ps).setString(4, objects.get(1).getName());

        verify(ps).executeUpdate();
    }
}

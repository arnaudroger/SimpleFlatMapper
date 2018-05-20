package org.simpleflatmapper.jdbc.test;


import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.jdbc.impl.ColumnMeta;
import org.simpleflatmapper.jdbc.impl.CrudFactory;
import org.simpleflatmapper.jdbc.impl.CrudMeta;
import org.simpleflatmapper.jdbc.impl.DatabaseMeta;
import org.simpleflatmapper.reflect.ReflectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MysqlCrudTest {



    @Test
    public void testBatch() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO `TEST`(id) VALUES(?), (?), (?), (?), (?), (?), (?), (?), (?), (?)")).thenReturn(ps);

        Crud<DbObject, Long> objectCrud =
            CrudFactory.<DbObject, Long>newInstance(
                    ReflectionService.newInstance().getClassMeta(DbObject.class),
                    ReflectionService.newInstance().getClassMeta(Long.class),
                    new CrudMeta(new DatabaseMeta("MySQL", 5, 5), null, "TEST", new ColumnMeta[]{new ColumnMeta("id", Types.INTEGER, true, null)}),
                    JdbcMapperFactory.newInstance());


        final List<DbObject> values = new ArrayList<DbObject>();
        for(int i = 0; i < 10; i ++) {
            values.add(DbObject.newInstance());
        }

        objectCrud.create(connection, values);

        for(int i = 0; i < 10; i++) {
            verify(ps).setLong(i + 1, values.get(i).getId());
        }
        verify(ps).executeUpdate();

    }

    @Test
    public void testSplitBatch() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        PreparedStatement ps2 = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO `TEST`(id) VALUES(?), (?), (?), (?), (?), (?), (?), (?), (?), (?)")).thenReturn(ps);
        when(connection.prepareStatement("INSERT INTO `TEST`(id) VALUES(?), (?), (?), (?), (?)")).thenReturn(ps2);
        when(ps.executeUpdate()).thenThrow(getPacketTooBigException());


        Crud<DbObject, Long> objectCrud =
                CrudFactory.<DbObject, Long>newInstance(
                        ReflectionService.newInstance().getClassMeta(DbObject.class),
                        ReflectionService.newInstance().getClassMeta(Long.class),
                        new CrudMeta(new DatabaseMeta("MySQL", 5, 5), null, "TEST", new ColumnMeta[]{new ColumnMeta("id", Types.INTEGER, true, null)}),
                        JdbcMapperFactory.newInstance());
        final int batchsize = 10;


        final List<DbObject> values = new ArrayList<DbObject>();
        for(int i = 0; i < batchsize; i ++) {
            values.add(DbObject.newInstance());
        }

        objectCrud.create(connection, values);

        for(int i = 0; i < batchsize; i++) {
            verify(ps).setLong(i + 1, values.get(i).getId());
        }
        verify(ps).executeUpdate();


        for(int i = 0; i < batchsize / 2; i++) {
            ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
            verify(ps2, times(2)).setLong(eq(i + 1), captor.capture());
            final List<Long> allValues = captor.getAllValues();
            assertEquals(values.get(i).getId(), allValues.get(0).longValue());
            assertEquals(values.get(i+ batchsize /2).getId(), allValues.get(1).longValue());
        }
        verify(ps).executeUpdate();


    }

    public static SQLException getPacketTooBigException() {
        try {
            return (SQLException) Class.forName("com.mysql.cj.jdbc.exceptions.PacketTooBigException").getConstructor(long.class, long.class).newInstance(60l, 60l);
        } catch (Exception e) {
            try {
                return (SQLException) Class.forName("com.mysql.jdbc.PacketTooBigException").getConstructor(long.class, long.class).newInstance(60l, 60l);
            } catch (Exception e1) {
                throw new Error(e1);
            }
        }
    }

}
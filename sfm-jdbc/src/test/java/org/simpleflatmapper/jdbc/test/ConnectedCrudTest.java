package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.ConnectedCrud;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.impl.DataSourceTransactionTemplate;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ListCollector;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConnectedCrudTest {

    @Test
    public void testDbObjectCrud() throws Exception {
        DataSource dataSource = DbHelper.getHsqlDataSource();

        ConnectedCrud<DbObject, Long> objectCrud =
                JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(dataSource, "TEST_DB_OBJECT");

        checkCrudDbObject(objectCrud, DbObject.newInstance());

        Connection connection = dataSource.getConnection();
        try {

            CrudTest.checkCrudDbObject(connection, objectCrud.crud(), DbObject.newInstance());

        } finally {
            connection.close();
        }
    }

    @Test
    public void testDelegateSuccess() throws SQLException {
        Crud<Object, Object> crud = mock(Crud.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);

        ConnectedCrud<Object, Object> connectedCrud = new ConnectedCrud<Object, Object>(new DataSourceTransactionTemplate(dataSource), crud);

        Collection<Object> values = new ArrayList<Object>();
        Object value = new Object();
        CheckedConsumer consumer = new CheckedConsumer() {
            @Override
            public void accept(Object o) throws Exception {
            }
        };

        connectedCrud.create(values);
        verify(crud).create(connection, values);
        verifyCloseCommitAndReset(crud, connection);

        connectedCrud.create(value);
        verify(crud).create(connection, value);
        verifyCloseCommitAndReset(crud, connection);


        connectedCrud.create(values, consumer);
        verify(crud).create(connection, values, consumer);
        verifyCloseCommitAndReset(crud, connection);

        connectedCrud.create(value, consumer);
        verify(crud).create(connection, value, consumer);
        verifyCloseCommitAndReset(crud, connection);


        // read
        connectedCrud.read(value);
        verify(crud).read(connection, value);
        verifyCloseCommitAndReset(crud, connection);

        connectedCrud.read(values, consumer);
        verify(crud).read(connection, values, consumer);
        verifyCloseCommitAndReset(crud, connection);

        // update

        connectedCrud.update(value);
        verify(crud).update(connection, value);
        verifyCloseCommitAndReset(crud, connection);

        connectedCrud.update(values);
        verify(crud).update(connection, values);
        verifyCloseCommitAndReset(crud, connection);


        // delete
        connectedCrud.delete(value);
        verify(crud).delete(connection, value);
        verifyCloseCommitAndReset(crud, connection);

        connectedCrud.delete(values);
        verify(crud).delete(connection, values);
        verifyCloseCommitAndReset(crud, connection);

        // create or update

        connectedCrud.createOrUpdate(value);
        verify(crud).createOrUpdate(connection, value);
        verifyCloseCommitAndReset(crud, connection);

        connectedCrud.createOrUpdate(values);
        verify(crud).createOrUpdate(connection, values);
        verifyCloseCommitAndReset(crud, connection);


        connectedCrud.createOrUpdate(value, consumer);
        verify(crud).createOrUpdate(connection, value, consumer);
        verifyCloseCommitAndReset(crud, connection);

        connectedCrud.createOrUpdate(values, consumer);
        verify(crud).createOrUpdate(connection, values, consumer);
        verifyCloseCommitAndReset(crud, connection);

    }

    @Test
    public void testDelegateFail() throws SQLException {
        Crud<Object, Object> crud = mock(Crud.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);

        ConnectedCrud<Object, Object> connectedCrud = new ConnectedCrud<Object, Object>(new DataSourceTransactionTemplate(dataSource), crud);

        Collection<Object> values = new ArrayList<Object>();
        Object value = new Object();
        CheckedConsumer consumer = new CheckedConsumer() {
            @Override
            public void accept(Object o) throws Exception {
            }
        };

        RuntimeException exception = new RuntimeException();
        doThrow(exception).when(connection).commit();

        try {
            connectedCrud.create(values);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).create(connection, values);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }

        try {
            connectedCrud.create(value);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).create(connection, value);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }


        try {
            connectedCrud.create(values, consumer);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).create(connection, values, consumer);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }

        try {
            connectedCrud.create(value, consumer);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).create(connection, value, consumer);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }


        // read
        try {
            connectedCrud.read(value);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).read(connection, value);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }

        try {
            connectedCrud.read(values, consumer);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).read(connection, values, consumer);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }
        // update

        try {
            connectedCrud.update(value);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).update(connection, value);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }

        try {
            connectedCrud.update(values);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).update(connection, values);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }


        // delete
        try {
            connectedCrud.delete(value);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).delete(connection, value);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }

        try {
            connectedCrud.delete(values);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).delete(connection, values);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }

        // create or update

        try {
            connectedCrud.createOrUpdate(value);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).createOrUpdate(connection, value);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }

        try {
            connectedCrud.createOrUpdate(values);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).createOrUpdate(connection, values);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }


        try {
            connectedCrud.createOrUpdate(value, consumer);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
            verify(crud).createOrUpdate(connection, value, consumer);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }

        try {
            connectedCrud.createOrUpdate(values, consumer);
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);

            verify(crud).createOrUpdate(connection, values, consumer);
            verifyCloseCommitFailAndReset(exception, crud, connection);
        }

    }


    private void verifyCloseCommitAndReset(Crud<Object, Object> crud, Connection connection) throws SQLException {
        verify(connection).commit();
        verify(connection).close();

        reset(crud, connection);
    }

    private void verifyCloseCommitFailAndReset(Exception exception, Crud<Object, Object> crud, Connection connection) throws SQLException {
        verify(connection).commit();
        verify(connection).rollback();
        verify(connection).close();

        reset(crud, connection);

        doThrow(exception).when(connection).commit();

    }

    @Test
    public void testDbObjectCrudTable() throws Exception {
        DataSource dataSource = DbHelper.getHsqlDataSource();

        ConnectedCrud<CrudTest.DbObjectTable, Long> objectCrud =
                JdbcMapperFactory.newInstance().<CrudTest.DbObjectTable, Long>crud(CrudTest.DbObjectTable.class, Long.class).to(dataSource);

        checkCrudDbObject(objectCrud, DbObject.newInstance(new CrudTest.DbObjectTable()));

        Connection connection = dataSource.getConnection();
        try {
            CrudTest.checkCrudDbObject(connection, objectCrud.crud(), DbObject.newInstance(new CrudTest.DbObjectTable()));
        } finally {
            connection.close();
        }
    }

    private <T extends DbObject> void checkCrudDbObject(ConnectedCrud<T, Long> objectCrud, T object) throws SQLException {
        assertNull(objectCrud.read(object.getId()));

        // create
        Long key =
                objectCrud.create(object, new CheckedConsumer<Long>() {
                    Long key;

                    @Override
                    public void accept(Long aLong) throws Exception {
                        key = aLong;
                    }
                }).key;

        assertNull(key);


        key = object.getId();
        // read
        assertEquals(object, objectCrud.read(key));

        object.setName("Udpdated");

        // update
        objectCrud.update(object);
        assertEquals(object, objectCrud.read(key));

        // delete
        objectCrud.delete(key);
        assertNull(objectCrud.read(key));

        objectCrud.create(object);

        assertEquals(object, objectCrud.where("id = :id", Long.class).readFirst(object.getId()));
        assertEquals(Arrays.asList(object), objectCrud.where("name = :name and id = :id", DbObject.class).read(object, new ListCollector<T>()).getList());
    }


}
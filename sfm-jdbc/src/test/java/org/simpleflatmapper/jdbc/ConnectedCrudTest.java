package org.simpleflatmapper.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.simpleflatmapper.jdbc.property.IndexedSetterProperty;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.MysqlDbHelper;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ListCollector;

import javax.persistence.Table;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;

public class ConnectedCrudTest {

    @Test
    public void testDbObjectCrud() throws Exception {
        DataSource dataSource = DbHelper.getHsqlDataSource();

        ConnectedCrud<DbObject, Long> objectCrud =
                JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(dataSource, "TEST_DB_OBJECT");

        checkCrudDbObject(objectCrud, DbObject.newInstance());

        Connection connection = dataSource.getConnection();
        try {

            CrudTest.checkCrudDbObject(connection, objectCrud, DbObject.newInstance());

        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectCrudTable() throws Exception {
        DataSource dataSource = DbHelper.getHsqlDataSource();

        ConnectedCrud<CrudTest.DbObjectTable, Long> objectCrud =
                JdbcMapperFactory.newInstance().<CrudTest.DbObjectTable, Long>crud(CrudTest.DbObjectTable.class, Long.class).to(dataSource);

        checkCrudDbObject( objectCrud, DbObject.newInstance(new CrudTest.DbObjectTable()));

        Connection connection = dataSource.getConnection();
        try {
            CrudTest.checkCrudDbObject(connection, objectCrud, DbObject.newInstance(new CrudTest.DbObjectTable()));
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
    }



}
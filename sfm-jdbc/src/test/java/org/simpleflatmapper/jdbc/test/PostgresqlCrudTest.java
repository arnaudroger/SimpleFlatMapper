package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PostgresqlCrudTest {

    @Test
    public void testBatchUpsertOnDb() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (connection == null) { System.err.println("Db Postgresql not available"); return; }
        System.out.println("connection = " + connection.getMetaData().getDatabaseMajorVersion()  + "." + connection.getMetaData().getDatabaseMinorVersion());
        if (connection.getMetaData().getDatabaseMajorVersion() != 9 || connection.getMetaData().getDatabaseMinorVersion() < 5)
            { System.err.println("Db Postgresql 9.5 not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

            DbObject object1 = DbObject.newInstance();
            DbObject object2 = DbObject.newInstance();

            objectCrud.create(connection, object1);

            object1.setName("BatchUpdate");
            object2.setName("BatchUpdate");

            objectCrud.createOrUpdate(connection, Arrays.<DbObject>asList(object1, object2));

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT count(*) FROM TEST_DB_OBJECT WHERE name = 'BatchUpdate'");
            assertTrue(resultSet.next());
            assertEquals(2, resultSet.getInt(1));

            assertEquals(object1, objectCrud.read(connection, object1.getId()));
            assertEquals(object2, objectCrud.read(connection, object2.getId()));

        } finally {
            connection.close();
        }
    }

    @Test
    public void testUpsert() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (connection == null) { System.err.println("Db MySQL not available"); return; }
        if (connection.getMetaData().getDatabaseMajorVersion() != 9 || connection.getMetaData().getDatabaseMinorVersion() < 5)
        { System.err.println("Db Postgresql 9.5 not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

            final DbObject object = DbObject.newInstance();
            objectCrud.createOrUpdate(connection, object);

            assertEquals(object, objectCrud.read(connection, object.getId()));

            object.setEmail("Updated Email");

            objectCrud.createOrUpdate(connection, object);

            assertEquals(object, objectCrud.read(connection, object.getId()));

        } finally {
            connection.close();
        }
    }

    @Test
    public void testUUID() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (connection == null) { System.err.println("Db POSTGRESQL not available"); return; }

        try {
            Crud<MyEntity, UUID> objectCrud =
                    JdbcMapperFactory
                            .newInstance()
                            .addKeys("uid")
                            .<MyEntity, UUID>crud(MyEntity.class, UUID.class)
                            .table(connection, "TEST_UUID");

            final MyEntity object = new MyEntity();
            object.setId(1);
            object.setUid(UUID.randomUUID());
            object.setName("n1");

            objectCrud.create(connection, object);

            assertEquals(object, objectCrud.read(connection, object.getUid()));

            object.setName("Updated Email");

            objectCrud.update(connection, object);

            assertEquals(object, objectCrud.read(connection, object.getUid()));

        } finally {
            connection.close();
        }

    }

    public static class MyEntity {

        private int _id;
        public int getId() { return _id; }
        public void setId( int value ) { _id = value; }

        private UUID _uid;
        public UUID getUid() { return _uid; }
        public void setUid( UUID value ) { _uid = value; }

        private String _name;
        public String getName() { return _name; }
        public void setName( String value ) { _name = value; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyEntity myEntity = (MyEntity) o;

            if (_id != myEntity._id) return false;
            if (_uid != null ? !_uid.equals(myEntity._uid) : myEntity._uid != null) return false;
            return _name != null ? _name.equals(myEntity._name) : myEntity._name == null;

        }

        @Override
        public int hashCode() {
            int result = _id;
            result = 31 * result + (_uid != null ? _uid.hashCode() : 0);
            result = 31 * result + (_name != null ? _name.hashCode() : 0);
            return result;
        }
    }
}
package org.sfm.datastax;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.annotations.Table;
import org.junit.Test;
import org.sfm.beans.DbObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DatastaxCrudTest extends AbstractDatastaxTest {

    @Test
    public void testCrud() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                DatastaxCrud<DbObject, Long> crud =
                        DatastaxMapperFactory.newInstance().crud(DbObject.class, Long.class).to(session, "dbobjects");
                testCrudDbObject(crud, DbObject.newInstance());
            }
        });
    }

    private <T extends DbObject> void testCrudDbObject(DatastaxCrud<T, Long> crud, T object) {

        assertNull(crud.read(object.getId()));

        crud.save(object);

        assertEquals(object, crud.read(object.getId()));

        object.setEmail("updated");

        crud.save(object);

        assertEquals(object, crud.read(object.getId()));

        crud.delete(object.getId());

        assertNull(crud.read(object.getId()));
    }

    @Test
    public void testCreateTTL() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                DatastaxCrud<DbObject, Long> crud =
                        DatastaxMapperFactory.newInstance().crud(DbObject.class, Long.class).to(session, "dbobjects");

                DbObject object = DbObject.newInstance();
                crud.saveWithTtl(object, 1);

                assertEquals(object, crud.read(object.getId()));
                Thread.sleep(2000);
                assertNull(crud.read(object.getId()));

                DbObject object2 = DbObject.newInstance();
                crud.save(object2, 1, System.currentTimeMillis());

                assertEquals(object2, crud.read(object2.getId()));
                Thread.sleep(2000);
                assertNull(crud.read(object2.getId()));


            }
        });
    }

    @Test
    public void testCreateDeleteTS() throws Exception {

        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {

                long ts = System.currentTimeMillis();

                DatastaxCrud<DbObject, Long> crud =
                        DatastaxMapperFactory.newInstance().crud(DbObject.class, Long.class).to(session, "dbobjects");

                DbObject object = DbObject.newInstance();
                crud.saveWithTimestamp(object, ts - 2000);

                assertEquals(object, crud.read(object.getId()));

                // delete before insert
                object.setEmail("Modified 1");
                crud.saveWithTimestamp(object, ts);
                crud.delete(object.getId(), ts - 1000);

                assertEquals(object, crud.read(object.getId()));


                // insert before delete
                object.setEmail("Modified 2");
                crud.delete(object.getId(), ts + 2000);
                crud.saveWithTimestamp(object, ts + 1000);

                assertNull(crud.read(object.getId()));


            }
        });
    }

    @Test
    public void testTableAnnotation() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                DatastaxCrud<DbObjectTable, Long> crud =
                        DatastaxMapperFactory.newInstance().crud(DbObjectTable.class, Long.class).to(session);
                testCrudDbObject(crud, DbObject.newInstance(new DbObjectTable()));
            }
        });
    }

    @Test
    public void testClassNameMatching() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                DatastaxCrud<DbObjects, Long> crud =
                        DatastaxMapperFactory.newInstance().crud(DbObjects.class, Long.class).to(session);
                testCrudDbObject(crud, DbObject.newInstance(new DbObjects()));
            }
        });

    }

    @Table(keyspace = "sfm", name = "dbobjects")
    public static class DbObjectTable extends DbObject {
    }

    public static class DbObjects extends DbObject {
    }
}

package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.annotations.Table;
import org.junit.Test;
import org.simpleflatmapper.datastax.DatastaxCrud;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.datastax.test.beans.DbObjectWithAlias;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class DatastaxCrudTest extends AbstractDatastaxTest {

    @Test
    public void testCrud() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                DatastaxCrud<DbObject, Long> crud =
                        DatastaxMapperFactory.newInstance().crud(DbObject.class, Long.class).to(session, "dbobjects");
                System.out.println("testCrud = " + crud);
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

                System.out.println("testCreateTTL = " + crud);
                DbObject object = DbObject.newInstance();
                crud.saveWithTtl(object, 2);
                Thread.sleep(500);

                assertEquals(object, crud.read(object.getId()));
                Thread.sleep(3000);
                assertNull(crud.read(object.getId()));

                DbObject object2 = DbObject.newInstance();
                crud.save(object2, 2, System.currentTimeMillis());
                Thread.sleep(500);

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
                System.out.println("testCreateDeleteTS = " + crud);
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

    @Test
    public void testCrudWithAlias() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                DatastaxCrud<DbObjectWithAlias, Long> crud =
                        DatastaxMapperFactory.newInstance().crud(DbObjectWithAlias.class, Long.class).to(session, "dbobjects");


                final DbObject object = DbObject.newInstance();

                final DatastaxCrud<DbObject, Long> crud2 =
                        DatastaxMapperFactory.newInstance().crud(DbObject.class, Long.class).to(session, "dbobjects");

                crud2.save(object);


                DbObjectWithAlias dbObjectWithAlias = crud.read(object.getId());

                assertNotNull(dbObjectWithAlias);

                assertEquals(object.getId(), dbObjectWithAlias.getIdWithAlias());
                assertEquals(object.getCreationTime(), dbObjectWithAlias.getCreationTimeWithAlias());
                assertEquals(object.getEmail(), dbObjectWithAlias.getEmailWithAlias());
                assertEquals(object.getName(), dbObjectWithAlias.getNameWithAlias());
            }
        });
    }

    @Table(keyspace = "sfm", name = "dbobjects")
    public static class DbObjectTable extends DbObject {
    }

    public static class DbObjects extends DbObject {
    }


}

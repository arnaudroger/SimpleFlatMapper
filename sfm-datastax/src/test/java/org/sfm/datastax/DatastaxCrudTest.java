package org.sfm.datastax;

import com.datastax.driver.core.Session;
import org.junit.Test;
import org.sfm.beans.DbObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DatastaxCrudTest extends AbstractDatastaxTest {

    DatastaxCrud<DbObject, Long> crud;
    @Test
    public void testCrud() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                crud =
                        DatastaxMapperFactory.newInstance().crud(DbObject.class, Long.class).to(session, "dbobjects");
            }
        });

        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                DbObject object = DbObject.newInstance();

                assertNull(crud.read(session, object.getId()));

                crud.save(session, object);

                assertEquals(object, crud.read(session, object.getId()));

                object.setEmail("updated");

                crud.save(session, object);

                assertEquals(object, crud.read(session, object.getId()));

                crud.delete(session, object.getId());

                assertNull(crud.read(session, object.getId()));

            }
        });
    }

    @Test
    public void testCreateTTL() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                DatastaxCrud<DbObject, Long> crud =
                        DatastaxMapperFactory.newInstance().crud(DbObject.class, Long.class).to(session, "dbobjects");

                DbObject object = DbObject.newInstance();
                crud.saveWithTtl(session, object, 1);

                assertEquals(object, crud.read(session, object.getId()));
                Thread.sleep(2000);
                assertNull(crud.read(session, object.getId()));

                DbObject object2 = DbObject.newInstance();
                crud.save(session, object2, 1, System.currentTimeMillis());

                assertEquals(object2, crud.read(session, object2.getId()));
                Thread.sleep(2000);
                assertNull(crud.read(session, object2.getId()));


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
                crud.saveWithTimestamp(session, object, ts - 2000);

                assertEquals(object, crud.read(session, object.getId()));

                // delete before insert
                object.setEmail("Modified 1");
                crud.saveWithTimestamp(session, object, ts);
                crud.delete(session, object.getId(), ts - 1000);

                assertEquals(object, crud.read(session, object.getId()));


                // insert before delete
                object.setEmail("Modified 2");
                crud.delete(session, object.getId(), ts + 2000);
                crud.saveWithTimestamp(session, object, ts + 1000);

                assertNull(crud.read(session, object.getId()));


            }
        });
    }
}

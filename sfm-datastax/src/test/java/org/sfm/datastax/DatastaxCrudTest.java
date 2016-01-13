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
}

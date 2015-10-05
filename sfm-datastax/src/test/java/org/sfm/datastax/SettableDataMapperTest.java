package org.sfm.datastax;

import com.datastax.driver.core.*;
import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.Mapper;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class SettableDataMapperTest extends AbstractDatastaxTest {

    @Test
    public void testInsertDbObjects() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {

                PreparedStatement preparedStatement = session.prepare("insert into " +
                        "dbobjects(id, name, email, creation_time, type_ordinal, type_name) " +
                        "values(?, ?, ?, ?, ?, ?)");
                DbObject dbObject = DbObject.newInstance();

                DatastaxBinder<DbObject> datastaxBinder = DatastaxMapperFactory.newInstance().mapFrom(DbObject.class);

                session.execute(datastaxBinder.mapTo(dbObject, preparedStatement));

                DatastaxMapper<DbObject> dbObjectDatastaxMapper = DatastaxMapperFactory.newInstance().mapTo(DbObject.class);
                DbObject actual = dbObjectDatastaxMapper.iterator(session.execute(session.prepare("select * from dbobjects where id = ?").bind(dbObject.getId()))).next();
                assertEquals(dbObject, actual);

                session.execute(session.prepare("delete from dbobjects where id = ?").bind(dbObject.getId()));
            }
        });
    }

    @Override
    protected void tearDown(Session session) {
        session.execute("DELETE FROM dbobjects where id = 2666");
    }
}
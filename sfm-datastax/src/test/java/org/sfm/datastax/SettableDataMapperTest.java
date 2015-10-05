package org.sfm.datastax;

import com.datastax.driver.core.*;
import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.Mapper;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class SettableDataMapperTest extends AbstractDatastaxTest {

    DbObject dbObject = DbObject.newInstance();

    @Test
    public void testInsertDbObjects() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {

                PreparedStatement preparedStatement = session.prepare("insert into " +
                        "dbobjects(id, name, email, creation_time, type_ordinal, type_name) " +
                        "values(?, ?, ?, ?, ?, ?)");

                DatastaxBinder<DbObject> datastaxBinder = DatastaxMapperFactory.newInstance().mapFrom(DbObject.class);

                session.execute(datastaxBinder.mapTo(dbObject, preparedStatement));

                DatastaxMapper<DbObject> dbObjectDatastaxMapper = DatastaxMapperFactory.newInstance().mapTo(DbObject.class);
                BoundStatement boundStatement = session.prepare("select * from dbobjects where id = ?").bind(dbObject.getId());
                ResultSet execute = session.execute(boundStatement);
                DbObject actual = dbObjectDatastaxMapper.iterator(execute).next();
                assertEquals(dbObject, actual);

            }
        });
    }

    @Override
    protected void tearDown(Session session) {
        session.execute(session.prepare("delete from dbobjects where id = ?").bind(dbObject.getId()));
    }
}
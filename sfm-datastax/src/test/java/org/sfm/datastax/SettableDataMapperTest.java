package org.sfm.datastax;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Batch;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.Mapper;
import org.sfm.reflect.TypeReference;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class SettableDataMapperTest extends AbstractDatastaxTest {

    public static final String QUERY = "insert into " +
            "dbobjects(id, name, email, creation_time, type_ordinal, type_name) " +
            "values(?, ?, ?, ?, ?, ?)";
    List<DbObject> dbObjects = Arrays.asList(DbObject.newInstance(), DbObject.newInstance());

    @Test
    public void testInsertDbObjects() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {

                PreparedStatement preparedStatement = session.prepare("insert into " +
                        "dbobjects(id, name, email, creation_time, type_ordinal, type_name) " +
                        "values(?, ?, ?, ?, ?, ?)");

                DatastaxBinder<DbObject> datastaxBinder = DatastaxMapperFactory.newInstance().mapFrom(DbObject.class);

                session.execute(datastaxBinder.mapTo(dbObjects.get(0), preparedStatement));
                checkObjectInserted(session, 0);

            }
        });
    }

    protected void checkObjectInserted(Session session, int i) {
        DbObject object = dbObjects.get(i);
        DatastaxMapper<DbObject> dbObjectDatastaxMapper = DatastaxMapperFactory.newInstance().mapTo(DbObject.class);
        BoundStatement boundStatement = session.prepare("select * from dbobjects where id = ?").bind(object.getId());
        ResultSet execute = session.execute(boundStatement);
        DbObject actual = dbObjectDatastaxMapper.iterator(execute).next();
        assertEquals(object, actual);
    }


    @Test
    public void testInsertDbObjectsBatch() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {


                Batch bs = QueryBuilder.batch();

                bs.add(new SimpleStatement(QUERY));
                bs.add(new SimpleStatement(QUERY));


                PreparedStatement preparedStatement = session.prepare(bs);

                DatastaxBinder<List<DbObject>> datastaxBinder = DatastaxMapperFactory.newInstance().mapFrom(new TypeReference<List<DbObject>>() {
                });

                session.execute(datastaxBinder.mapTo(dbObjects, preparedStatement));

                checkObjectInserted(session, 0);
                checkObjectInserted(session, 1);

            }
        });
    }

}
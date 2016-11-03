package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Batch;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.junit.Test;
import org.simpleflatmapper.datastax.DatastaxBinder;
import org.simpleflatmapper.datastax.DatastaxMapper;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithTuple;
import org.simpleflatmapper.util.TypeReference;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SettableDataMapperTest extends AbstractDatastaxTest {

    public static final String QUERY = "insert into " +
            "dbobjects(id, name, email, creation_time, type_ordinal, type_name) " +
            "values(?, ?, ?, ?, ?, ?)";
    List<DbObject> dbObjects = Arrays.asList(DbObject.newInstance(), DbObject.newInstance());


    @Test
    public void testUpdateDbObjectWithTupleWithDatatype() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithTuple> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithTuple.class);
                DbObjectsWithTuple value = mapper.iterator(session.execute("select id, t from dbobjects_tuple")).next();
                PreparedStatement preparedStatement =  session.prepare("update dbobjects_tuple SET t  = ? where id = ?");
                DatastaxBinder<DbObjectsWithTuple> datastaxBinder = DatastaxMapperFactory.newInstance().mapFrom(DbObjectsWithTuple.class);
                session.execute(datastaxBinder.mapTo(value, preparedStatement));
            }
        });
    }

    @Test
    public void testInsertDbObjects() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {

                PreparedStatement preparedStatement = session.prepare(QUERY);

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

                DatastaxBinder<List<DbObject>> datastaxBinder = DatastaxMapperFactory.newInstance().useAsm(false).mapFrom(new TypeReference<List<DbObject>>() {
                });

                Statement statement = datastaxBinder.mapTo(dbObjects, preparedStatement);

                statement.enableTracing();
                session.execute(statement);

                checkObjectInserted(session, 0);
                checkObjectInserted(session, 1);

            }
        });
    }

    @Test
    public void testUpdateDbObjectsBatch() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {


                PreparedStatement preparedStatement = session.prepare(QUERY);

                DatastaxBinder<DbObject> datastaxBinder = DatastaxMapperFactory.newInstance().mapFrom(DbObject.class);

                DbObject value = dbObjects.get(0);

                session.execute(datastaxBinder.mapTo(value, preparedStatement));
                checkObjectInserted(session, 0);

                PreparedStatement updateStatement = session.prepare("UPDATE dbobjects SET name = ? WHERE id = ?");
                value.setName("newname");

                session.execute(datastaxBinder.mapTo(value, updateStatement));

                checkObjectInserted(session, 0);
            }
        });
    }

}
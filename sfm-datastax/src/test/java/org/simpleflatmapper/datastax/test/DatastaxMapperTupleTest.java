package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.simpleflatmapper.datastax.DatastaxMapper;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.tuple.Tuple3;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithTuple;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithTupleValue;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class DatastaxMapperTupleTest extends AbstractDatastaxTest {


    @Test
    public void testMapTupleToSfmTuple() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithTuple> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithTuple.class);

                ResultSet rs = session.execute("select id, t from dbobjects_tuple");

                final Iterator<DbObjectsWithTuple> iterator = mapper.iterator(rs);

                DbObjectsWithTuple next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals(new Tuple3<String, Long, Integer>("t1", 12l, 13), next.getT());

                assertFalse(iterator.hasNext());

            }
        });
    }

    @Test
    public void testMapTupleToTupleValue() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithTupleValue> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithTupleValue.class);

                ResultSet rs = session.execute("select id, t from dbobjects_tuple");

                final Iterator<DbObjectsWithTupleValue> iterator = mapper.iterator(rs);

                DbObjectsWithTupleValue next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals("t1", next.getT().getString(0));
                assertEquals(12l, next.getT().getLong(1));
                assertEquals(13, next.getT().getInt(2));

                assertFalse(iterator.hasNext());

            }
        });
    }

}
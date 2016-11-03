package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.simpleflatmapper.datastax.DatastaxMapper;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithUDT;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithUDTTupleList;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class DatastaxMapperUDTTest extends AbstractDatastaxTest {


    @Test
    public void testMapUDT() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithUDT> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithUDT.class);

                ResultSet rs = session.execute("select id, t from dbobjects_udt");

                final Iterator<DbObjectsWithUDT> iterator = mapper.iterator(rs);

                DbObjectsWithUDT next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals("t1", next.getT().str);
                assertEquals(12l, next.getT().l);

                assertFalse(iterator.hasNext());

            }
        });
    }

    @Test
    public void testMapUDTTupleList() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithUDTTupleList> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithUDTTupleList.class);

                ResultSet rs = session.execute("select id, t from dbobjects_udttyplelist");

                final Iterator<DbObjectsWithUDTTupleList> iterator = mapper.iterator(rs);

                DbObjectsWithUDTTupleList next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals("t1", next.getT().str);
                assertEquals(Long.valueOf(12l), next.getT().t.first());
                assertEquals(Arrays.asList(13, 14), next.getT().t.second());

                assertFalse(iterator.hasNext());

            }
        });
    }
}
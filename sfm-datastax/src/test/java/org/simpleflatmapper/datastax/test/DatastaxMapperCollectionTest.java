package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.simpleflatmapper.datastax.DatastaxMapper;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithCollection;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithCollectionLong;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithCollectionTuple;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithCollectionUDT;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithList;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithMap;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithMapLongLong;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithMapUDT;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.*;

public class DatastaxMapperCollectionTest extends AbstractDatastaxTest {

    @Test
    public void testMapSetToSet() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithSet> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithSet.class);

                ResultSet rs = session.execute("select id, emails from dbobjects_set");

                final Iterator<DbObjectsWithSet> iterator = mapper.iterator(rs);

                DbObjectsWithSet next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals(new HashSet<String>(Arrays.asList("a@a", "b@b")), next.getEmails());

                assertFalse(iterator.hasNext());

            }
        });
    }

    @Test
    public void testMapSetToCollection() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithCollection> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithCollection.class);

                ResultSet rs = session.execute("select id, emails from dbobjects_set");

                final Iterator<DbObjectsWithCollection> iterator = mapper.iterator(rs);

                DbObjectsWithCollection next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals(new HashSet<String>(Arrays.asList("a@a", "b@b")), next.getEmails());

                assertFalse(iterator.hasNext());

            }
        });
    }

    @Test
    public void testMapSetIntToSetLong() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithCollectionLong> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithCollectionLong.class);

                ResultSet rs = session.execute("select id, l from dbobjects_setint");

                final Iterator<DbObjectsWithCollectionLong> iterator = mapper.iterator(rs);

                DbObjectsWithCollectionLong next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals(new HashSet<Long>(Arrays.asList(Long.valueOf(13), Long.valueOf(14))), next.getL());

                assertFalse(iterator.hasNext());

            }
        });
    }
    @Test
    public void testMapSetUDT() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithCollectionUDT> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithCollectionUDT.class);

                ResultSet rs = session.execute("select id, l from dbobjects_setudt");

                final Iterator<DbObjectsWithCollectionUDT> iterator = mapper.iterator(rs);

                DbObjectsWithCollectionUDT next = iterator.next();

                assertEquals(1, next.getId());
                DbObjectsWithCollectionUDT.MyType myType = next.getL().iterator().next();
                assertEquals("t1", myType.str);
                assertEquals(12, myType.l);

                assertFalse(iterator.hasNext());

            }
        });
    }
    @Test
    public void testMapListToList() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithList> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithList.class);

                ResultSet rs = session.execute("select id, emails from dbobjects_list");

                final Iterator<DbObjectsWithList> iterator = mapper.iterator(rs);

                DbObjectsWithList next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals(Arrays.asList("a@a", "b@b"), next.getEmails());

                assertFalse(iterator.hasNext());

            }
        });
    }

    @Test
    public void testMapListIntToListLong() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithCollectionLong> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithCollectionLong.class);

                ResultSet rs = session.execute("select id, l from dbobjects_listint");

                final Iterator<DbObjectsWithCollectionLong> iterator = mapper.iterator(rs);

                DbObjectsWithCollectionLong next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals(Arrays.asList(Long.valueOf(13), Long.valueOf(14)), next.getL());

                assertFalse(iterator.hasNext());

            }
        });
    }

    @Test
    public void testMapListUDT() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithCollectionUDT> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithCollectionUDT.class);

                ResultSet rs = session.execute("select id, l from dbobjects_listudt");

                final Iterator<DbObjectsWithCollectionUDT> iterator = mapper.iterator(rs);

                DbObjectsWithCollectionUDT next = iterator.next();

                assertEquals(1, next.getId());
                DbObjectsWithCollectionUDT.MyType myType = next.getL().iterator().next();
                assertEquals("t1", myType.str);
                assertEquals(12, myType.l);

                assertFalse(iterator.hasNext());

            }
        });
    }

    @Test
    public void testMapListTuple() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithCollectionTuple> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithCollectionTuple.class);

                ResultSet rs = session.execute("select id, l from dbobjects_listtuple");

                final Iterator<DbObjectsWithCollectionTuple> iterator = mapper.iterator(rs);

                DbObjectsWithCollectionTuple next = iterator.next();

                assertEquals(1, next.getId());
                Tuple2<String, Long> tuple2 = next.getL().iterator().next();
                assertEquals("t1", tuple2.first());
                assertEquals(new Long(12), tuple2.second());

                assertFalse(iterator.hasNext());

            }
        });
    }

    @Test
    public void testMapListToCollection() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithCollection> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithCollection.class);

                ResultSet rs = session.execute("select id, emails from dbobjects_list");

                final Iterator<DbObjectsWithCollection> iterator = mapper.iterator(rs);

                DbObjectsWithCollection next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals(Arrays.asList("a@a", "b@b"), next.getEmails());

                assertFalse(iterator.hasNext());

            }
        });
    }


    @Test
    public void testMapMap() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithMap> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithMap.class);

                ResultSet rs = session.execute("select id, emails from dbobjects_map");

                final Iterator<DbObjectsWithMap> iterator = mapper.iterator(rs);

                DbObjectsWithMap next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals("a@a", next.getEmails().get(10));
                assertEquals("b@b", next.getEmails().get(23));

                assertFalse(iterator.hasNext());

            }
        });
    }
    @Test
    public void testMapMapWithConverter() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithMapLongLong> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithMapLongLong.class);

                ResultSet rs = session.execute("select id, ll from dbobjects_mapll");

                final Iterator<DbObjectsWithMapLongLong> iterator = mapper.iterator(rs);

                DbObjectsWithMapLongLong next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals(new Long(100l), next.getLl().get(new Long(10l)));
                assertEquals(new Long(200l), next.getLl().get(new Long(20l)));

                assertFalse(iterator.hasNext());

            }
        });
    }

    @Test
    public void testMapMapWithUDT() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObjectsWithMapUDT> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObjectsWithMapUDT.class);

                ResultSet rs = session.execute("select id, l from dbobjects_mapudt");

                final Iterator<DbObjectsWithMapUDT> iterator = mapper.iterator(rs);

                DbObjectsWithMapUDT next = iterator.next();

                assertEquals(1, next.getId());
                DbObjectsWithMapUDT.MyType myType = next.getL().get(2);
                assertEquals("t1", myType.str);
                assertEquals(12, myType.l);

                assertFalse(iterator.hasNext());

            }
        });
    }
}
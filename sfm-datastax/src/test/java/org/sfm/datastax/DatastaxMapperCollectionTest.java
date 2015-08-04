package org.sfm.datastax;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.sfm.datastax.beans.DbObjectsWithCollection;
import org.sfm.datastax.beans.DbObjectsWithList;
import org.sfm.datastax.beans.DbObjectsWithMap;
import org.sfm.datastax.beans.DbObjectsWithSet;

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

}
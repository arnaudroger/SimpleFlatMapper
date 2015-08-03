package org.sfm.datastax;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.sfm.datastax.beans.DbObjectsWithSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.*;


public class DatastaxMapperSetTest extends AbstractDatastaxTest {

    @Test
    public void testMapSetToCollection() throws Exception {
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


}
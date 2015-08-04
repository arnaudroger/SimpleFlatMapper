package org.sfm.datastax;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.sfm.datastax.beans.DbObjectsWithTuple;
import org.sfm.datastax.beans.DbObjectsWithTupleValue;
import org.sfm.datastax.beans.DbObjectsWithUDT;
import org.sfm.tuples.Tuple3;

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


}
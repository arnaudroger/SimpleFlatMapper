package org.sfm.datastax;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sfm.datastax.beans.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.tuples.Tuple3;
import org.sfm.utils.LibrarySet;
import org.sfm.utils.MultiClassLoaderJunitRunner;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


@RunWith(MultiClassLoaderJunitRunner.class)
@LibrarySet(
        libraryGroups = {
                //IFJAVA8_START
                "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/3.0.3/cassandra-driver-core-3.0.3.jar",
                //IFJAVA8_END
                "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/2.1.8/cassandra-driver-core-2.1.8.jar"
        },
        includes={ReflectionService.class, DatastaxCrud.class, DatastaxCrudTest.class}
)
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
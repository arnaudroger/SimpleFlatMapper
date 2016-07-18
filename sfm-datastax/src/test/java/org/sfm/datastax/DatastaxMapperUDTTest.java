package org.sfm.datastax;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sfm.datastax.beans.DbObjectsWithTuple;
import org.sfm.datastax.beans.DbObjectsWithTupleValue;
import org.sfm.datastax.beans.DbObjectsWithUDT;
import org.sfm.datastax.beans.DbObjectsWithUDTTupleList;
import org.sfm.reflect.ReflectionService;
import org.sfm.tuples.Tuple3;
import org.sfm.utils.LibrarySet;
import org.sfm.utils.MultiClassLoaderJunitRunner;

import java.util.Arrays;
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
        includes={ReflectionService.class, DatastaxCrud.class, DatastaxCrudTest.class},
        names={"v303", "v218"}
)
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
package org.sfm.datastax;

import com.datastax.driver.core.*;
import org.cassandraunit.AbstractCassandraUnit4TestCase;
import org.cassandraunit.CassandraUnit;
import org.cassandraunit.dataset.DataSet;
import org.cassandraunit.dataset.json.ClassPathJsonDataSet;
import org.cassandraunit.dataset.yaml.ClassPathYamlDataSet;
import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.TestAffinityObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;


public class DatastaxMapperFactoryMapperTest extends AbstractDatastaxTest {


    @Test
    public void testDynamicMapper() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObject> mapper = DatastaxMapperFactory.newInstance().newMapper(DbObject.class);

                ResultSet rs = session.execute("select id, name, email, creation_time, type_ordinal, type_name from dbobjects");

                final Iterator<DbObject> iterator = mapper.iterator(rs);

                DbObject next = iterator.next();

                assertEquals(1, next.getId());
                assertEquals("Arnaud Roger", next.getName());
                assertEquals("arnaud.roger@gmail.com", next.getEmail());
                assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2012-10-02 12:10:10"), next.getCreationTime());
                assertEquals(DbObject.Type.type2, next.getTypeOrdinal());
                assertEquals(DbObject.Type.type3, next.getTypeName());

                assertFalse(iterator.hasNext());

                rs = session.execute("select id, name from dbobjects");

                next = mapper.iterator(rs).next();

                assertEquals(1, next.getId());
                assertEquals("Arnaud Roger", next.getName());
                assertNull(next.getEmail());
                assertNull(next.getCreationTime());
                assertNull(next.getTypeOrdinal());
                assertNull(next.getTypeName());

                rs = session.execute("select id, email from dbobjects");

                next = mapper.iterator(rs).next();

                assertEquals(1, next.getId());
                assertNull(next.getName());
                assertEquals("arnaud.roger@gmail.com", next.getEmail());
                assertNull(next.getCreationTime());
                assertNull(next.getTypeOrdinal());
                assertNull(next.getTypeName());

                rs = session.execute("select id, type_ordinal from dbobjects");

                next = mapper.iterator(rs).next();

                assertEquals(1, next.getId());
                assertNull(next.getName());
                assertNull(next.getEmail());
                assertNull(next.getCreationTime());
                assertEquals(DbObject.Type.type2, next.getTypeOrdinal());
                assertNull(next.getTypeName());

            }
        });
    }

    @Test
    public void testAlias() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<DbObject> mapper = DatastaxMapperFactory.newInstance().addAlias("firstname", "name").newMapper(DbObject.class);
                ResultSet rs = session.execute("select id, email as firstname from dbobjects");

                final Iterator<DbObject> iterator = mapper.iterator(rs);

                DbObject o = iterator.next();

                assertEquals(1, o.getId());
                assertEquals("arnaud.roger@gmail.com", o.getName());

            }
        });
    }

    @Test
    public void testTypeAffinity() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DatastaxMapper<TestAffinityObject> mapper = DatastaxMapperFactory.newInstance().newMapper(TestAffinityObject.class);
                ResultSet rs = session.execute("select id as fromInt, email as fromString from dbobjects");

                final Iterator<TestAffinityObject> iterator = mapper.iterator(rs);

                TestAffinityObject o = iterator.next();

                assertEquals(1, o.fromInt.i);
                assertNull(o.fromInt.str);
                assertEquals("arnaud.roger@gmail.com", o.fromString.str);
                assertEquals(0, o.fromString.i);

            }
        });
    }


}
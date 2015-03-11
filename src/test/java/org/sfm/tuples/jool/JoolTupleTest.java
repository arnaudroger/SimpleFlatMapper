package org.sfm.tuples.jool;

import org.jooq.lambda.tuple.Tuple3;
import org.junit.Test;
import org.sfm.csv.CsvParser;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.Assert.*;

public class JoolTupleTest {



    @Test
    public void testMetaDataOnJoolTuple() throws Exception {


        //creates a new tuple allocated on the JVM heap

        System.out.println("super " + Tuple3.class.toString());
        for(Class<?> clazz : Tuple3.class.getInterfaces()) {
            System.out.println("I " + clazz.toString());

        }

        ClassMeta<Tuple3<Long, Integer, Short>> cm =
                ReflectionService.newInstance().getClassMeta(new TypeReference<Tuple3<Long, Integer, Short>>(){}.getType(), false);

        final PropertyFinder<Tuple3<Long, Integer, Short>> propertyFinder = cm.newPropertyFinder();

        final PropertyMeta<Tuple3<Long, Integer, Short>, Long> fieldA = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt0", 0, true, true));
        final PropertyMeta<Tuple3<Long, Integer, Short>, Integer> fieldB = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt1", 0, true, true));
        final PropertyMeta<Tuple3<Long, Integer, Short>, Short> fieldC = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt2", 0, true, true));
        final PropertyMeta<Tuple3<Long, Integer, Short>, ?> fieldD = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt3", 0, true, true));

        assertNotNull(fieldA);
        assertNotNull(fieldB);
        assertNotNull(fieldC);
        assertNull(fieldD);

        Tuple3<Long, Integer, Short> tuple = new Tuple3<Long, Integer, Short>(6l, 7, (short)3);

        assertTrue(fieldA instanceof ConstructorPropertyMeta);
        assertTrue(fieldB instanceof ConstructorPropertyMeta);
        assertTrue(fieldC instanceof ConstructorPropertyMeta);

        assertEquals(6l, fieldA.getGetter().get(tuple).longValue());
        assertEquals(7, fieldB.getGetter().get(tuple).intValue());
        assertEquals(3, fieldC.getGetter().get(tuple).shortValue());

    }

    @Test
    public void testCsvParser() throws IOException {
        final CsvParser.StaticMapToDSL<Tuple3<Long, Integer, Short>> mapToDSL = CsvParser.mapTo(new TypeReference<Tuple3<Long, Integer, Short>>() {
        }).defaultHeaders();
        final Iterator<Tuple3<Long, Integer, Short>> iterator = mapToDSL.iterator(new StringReader("6,7,3\n7,8,9"));

        final Tuple3<Long, Integer, Short> tuple1 = iterator.next();

        assertEquals(6l, tuple1.v1().longValue());
        assertEquals(7, tuple1.v2().intValue());
        assertEquals((short)3, tuple1.v3().shortValue());

        final Tuple3<Long, Integer, Short> tuple2 = iterator.next();

        assertEquals(7l, tuple2.v1().longValue());
        assertEquals(8, tuple2.v2().intValue());
        assertEquals((short)9, tuple2.v3().shortValue());
    }
}

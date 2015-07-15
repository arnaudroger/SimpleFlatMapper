package org.sfm.tuples.fasttuple;

import com.boundary.tuple.FastTuple;
import com.boundary.tuple.TupleSchema;
import org.junit.Before;
import org.junit.Test;
import org.sfm.csv.CsvParser;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyFinder;
import org.sfm.reflect.meta.PropertyMeta;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

public class FastTupleTest {

    private FastTuple tuple;

    @Before
    public void setUp() throws Exception {
        TupleSchema schema = TupleSchema.builder().
                addField("fieldA", Long.TYPE).
                addField("fieldB", Integer.TYPE).
                addField("fieldC", Short.TYPE).
                heapMemory().
                build();

        tuple = schema.createTuple();
    }

    @Test
    public void testMetaDataOnFastTuple() throws Exception {
        //creates a new tuple allocated on the JVM heap
        ClassMeta<FastTuple> cm = ReflectionService.newInstance().getClassMeta(tuple.getClass());

        final PropertyFinder<FastTuple> propertyFinder = cm.newPropertyFinder();

        final PropertyMeta<FastTuple, Long> fieldA = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldA", 0, true, true));
        final PropertyMeta<FastTuple, Integer> fieldB = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldB", 0, true, true));
        final PropertyMeta<FastTuple, Short> fieldC = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldC", 0, true, true));
        final PropertyMeta<FastTuple, ?> fieldD = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldD", 0, true, true));

        assertNotNull(fieldA);
        assertNotNull(fieldB);
        assertNotNull(fieldC);
        assertNull(fieldD);


        fieldA.getSetter().set(tuple, 6l);
        assertEquals(6l, fieldA.getGetter().get(tuple).longValue());

        fieldB.getSetter().set(tuple, 7);
        assertEquals(7, fieldB.getGetter().get(tuple).intValue());

        fieldC.getSetter().set(tuple, (short)3);
        assertEquals(3, fieldC.getGetter().get(tuple).shortValue());

        System.out.println(Arrays.toString(cm.generateHeaders()));
        assertArrayEquals(new String[]{"fieldA", "fieldB", "fieldC"}, cm.generateHeaders());
    }

    @Test
    public void testMetaDataOnFastTupleDirectMemory() throws Exception {

        final TupleSchema tupleSchema = TupleSchema.builder().
                addField("fieldA", Long.TYPE).
                addField("fieldB", Integer.TYPE).
                addField("fieldC", Short.TYPE).
                directMemory().
                build();

        final FastTuple tuple = tupleSchema.createTuple();

        //creates a new tuple allocated on the JVM heap
        ClassMeta<FastTuple> cm = ReflectionService.newInstance().getClassMeta(tuple.getClass());

        final PropertyFinder<FastTuple> propertyFinder = cm.newPropertyFinder();

        final PropertyMeta<FastTuple, Long> fieldA = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldA", 0, true, true));
        final PropertyMeta<FastTuple, Integer> fieldB = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldB", 0, true, true));
        final PropertyMeta<FastTuple, Short> fieldC = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldC", 0, true, true));
        final PropertyMeta<FastTuple, ?> fieldD = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldD", 0, true, true));

        assertNotNull(fieldA);
        assertNotNull(fieldB);
        assertNotNull(fieldC);
        assertNull(fieldD);


        fieldA.getSetter().set(tuple, 6l);
        assertEquals(6l, fieldA.getGetter().get(tuple).longValue());

        fieldB.getSetter().set(tuple, 7);
        assertEquals(7, fieldB.getGetter().get(tuple).intValue());

        fieldC.getSetter().set(tuple, (short)3);
        assertEquals(3, fieldC.getGetter().get(tuple).shortValue());

        try {
            cm.generateHeaders();
            fail();
        } catch(Exception e) {}
    }

    @Test
    public void testCsvParser() throws IOException {
        final CsvParser.StaticMapToDSL<? extends FastTuple> mapToDSL = CsvParser.mapTo(tuple.getClass()).defaultHeaders();
        final Iterator<? extends FastTuple> iterator = mapToDSL.iterator(new StringReader("6,7,3\n7,8,9"));

        final FastTuple tuple1 = iterator.next();

        assertEquals(6l, tuple1.getLong(1));
        assertEquals(7, tuple1.getInt(2));
        assertEquals((short)3, tuple1.getShort(3));

        final FastTuple tuple2 = iterator.next();

        assertEquals(7l, tuple2.getLong(1));
        assertEquals(8, tuple2.getInt(2));
        assertEquals((short)9, tuple2.getShort(3));
    }
}

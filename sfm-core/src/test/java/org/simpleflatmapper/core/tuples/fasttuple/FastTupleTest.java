package org.simpleflatmapper.core.tuples.fasttuple;

import com.boundary.tuple.FastTuple;
import com.boundary.tuple.TupleSchema;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.core.reflect.ReflectionService;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;
import org.simpleflatmapper.core.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.core.reflect.meta.PropertyFinder;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;

import java.util.Arrays;

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
        ClassMeta<FastTuple> cm = ReflectionService.newInstance().getClassMeta((Class<FastTuple>) tuple.getClass());

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
        ClassMeta<FastTuple> cm = ReflectionService.newInstance().getClassMeta((Class<FastTuple>) tuple.getClass());

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
}

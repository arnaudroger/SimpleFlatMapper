package org.simpleflatmapper.reflect.test.tuples.fasttuple;

import com.boundary.tuple.FastTuple;
import com.boundary.tuple.TupleSchema;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.ConstantPredicate;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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
    PropertyFinder.PropertyFilter predicate = new PropertyFinder.PropertyFilter(ConstantPredicate.<PropertyMeta<?, ?>>truePredicate());

    @Test
    public void testMetaDataOnFastTuple() throws Exception {
        //creates a new tuple allocated on the JVM heap
        @SuppressWarnings("unchecked") ClassMeta<FastTuple> cm = ReflectionService.newInstance().getClassMeta((Class<FastTuple>) tuple.getClass());

        final PropertyFinder<FastTuple> propertyFinder = cm.newPropertyFinder();

        final PropertyMeta<FastTuple, Long> fieldA = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldA", 0, true, true), new Object[0], (TypeAffinity)null, predicate);
        final PropertyMeta<FastTuple, Integer> fieldB = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldB", 0, true, true), new Object[0], (TypeAffinity)null, predicate);
        final PropertyMeta<FastTuple, Short> fieldC = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldC", 0, true, true), new Object[0], (TypeAffinity)null, predicate);
        final PropertyMeta<FastTuple, ?> fieldD = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldD", 0, true, true), new Object[0], (TypeAffinity)null, predicate);

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
        @SuppressWarnings("unchecked") ClassMeta<FastTuple> cm = ReflectionService.newInstance().getClassMeta((Class<FastTuple>) tuple.getClass());

        final PropertyFinder<FastTuple> propertyFinder = cm.newPropertyFinder();

        final PropertyMeta<FastTuple, Long> fieldA = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldA", 0, true, true), new Object[0], (TypeAffinity)null, predicate);
        final PropertyMeta<FastTuple, Integer> fieldB = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldB", 0, true, true), new Object[0], (TypeAffinity)null, predicate);
        final PropertyMeta<FastTuple, Short> fieldC = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldC", 0, true, true), new Object[0], (TypeAffinity)null, predicate);
        final PropertyMeta<FastTuple, ?> fieldD = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldD", 0, true, true), new Object[0], (TypeAffinity)null, predicate);

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

    }
}

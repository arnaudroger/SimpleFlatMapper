package org.sfm.tuples.jool;

import com.boundary.tuple.FastTuple;
import com.boundary.tuple.TupleSchema;
import org.junit.Test;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyFinder;
import org.sfm.reflect.meta.PropertyMeta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class JoolTupleTest {



    @Test
    public void testMetaDataOnFastTuple() throws Exception {
        TupleSchema schema = TupleSchema.builder().
                addField("fieldA", Long.TYPE).
                addField("fieldB", Integer.TYPE).
                addField("fieldC", Short.TYPE).
                heapMemory().
                build();

        //creates a new tuple allocated on the JVM heap
        FastTuple tuple = schema.createTuple();

        System.out.println("super " + tuple.getClass().getSuperclass().toString());
        for(Class<?> clazz : tuple.getClass().getInterfaces()) {
            System.out.println("I " + clazz.toString());

        }
        System.out.println(tuple.getClass().getClassLoader());

        ClassMeta<FastTuple> cm = ReflectionService.newInstance().getClassMeta(tuple.getClass(), true);

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

    }

    @Test
    public void testCsvParser() {

    }
}

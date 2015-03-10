package org.sfm.reflect.fasttuple;

import com.boundary.tuple.FastTuple;
import com.boundary.tuple.TupleSchema;
import org.junit.Test;
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyFinder;
import org.sfm.reflect.meta.PropertyMeta;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FastTupleTest {



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
        ClassMeta<FastTuple> cm = ReflectionService.newInstance().getClassMeta(tuple.getClass(), true);

        final PropertyFinder<FastTuple> propertyFinder = cm.newPropertyFinder();

        final PropertyMeta<FastTuple, ?> fieldA = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldA", 0, true, true));

        assertNotNull(fieldA);
        System.out.println(fieldA.toString());

        final PropertyMeta<FastTuple, ?> fieldB = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldB", 0, true, true));

        assertNotNull(fieldB);

        final PropertyMeta<FastTuple, ?> fieldC = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldC", 0, true, true));

        assertNotNull(fieldC);

        final PropertyMeta<FastTuple, ?> fieldD = propertyFinder.findProperty(new DefaultPropertyNameMatcher("fieldD", 0, true, true));

        assertNull(fieldD);

    }
}

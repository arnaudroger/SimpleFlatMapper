package org.simpleflatmapper.core.reflect.tuples.jool;

import org.jooq.lambda.tuple.Tuple3;
import org.junit.Test;
import org.simpleflatmapper.core.reflect.InstantiatorDefinition;
import org.simpleflatmapper.core.reflect.ReflectionService;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;
import org.simpleflatmapper.core.reflect.meta.ConstructorPropertyMeta;
import org.simpleflatmapper.core.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.core.reflect.meta.PropertyFinder;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.TypeReference;

import java.lang.reflect.Type;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JoolTupleTest {



    @Test
    public void testMetaDataOnJoolTuple() throws Exception {


        //creates a new tuple allocated on the JVM heap

        System.out.println("super " + Tuple3.class.toString());
        for(Class<?> clazz : Tuple3.class.getInterfaces()) {
            System.out.println("I " + clazz.toString());

        }

        ClassMeta<Tuple3<Long, Integer, Short>> cm =
                ReflectionService.newInstance().getClassMeta(new TypeReference<Tuple3<Long, Integer, Short>>(){}.getType());

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
    public void testGenerateHeadersJoolTuple() {
        String[] names = {"element0", "element1"};
        ClassMeta<Object> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<org.jooq.lambda.tuple.Tuple2<String, String>>() {
        }.getType());
        assertArrayEquals(
                names,
                classMeta.generateHeaders());
    }
    @Test
    public void testFindPropertyNoAsmJool() {
        Type type = new TypeReference<org.jooq.lambda.tuple.Tuple2<String, String>>() {}.getType();

        ClassMeta<org.jooq.lambda.tuple.Tuple2<String, String>> classMeta = ReflectionService.disableAsm().getClassMeta(type);

        InstantiatorDefinition instantiatorDefinition = classMeta.getInstantiatorDefinitions().get(0);

        assertEquals("v1", instantiatorDefinition.getParameters()[0].getName());
        assertEquals("v2", instantiatorDefinition.getParameters()[1].getName());
        assertEquals(2, instantiatorDefinition.getParameters().length);
    }

}

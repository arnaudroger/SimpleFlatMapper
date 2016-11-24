package org.simpleflatmapper.reflect.test.tuples.jool;

import org.jooq.lambda.tuple.Tuple3;
import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ConstructorPropertyMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.lang.reflect.Type;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JoolTupleTest {

    private Predicate<PropertyMeta<?, ?>> isValidPropertyMeta = ConstantPredicate.truePredicate();


    @Test
    public void testMetaDataOnJoolTuple() throws Exception {


        //creates a new tuple allocated on the JVM heap

        System.out.println("super " + Tuple3.class.toString());
        for(Class<?> clazz : Tuple3.class.getInterfaces()) {
            System.out.println("I " + clazz.toString());

        }

        ClassMeta<Tuple3<Long, Integer, Short>> cm =
                ReflectionService.newInstance().getClassMeta(new TypeReference<Tuple3<Long, Integer, Short>>(){}.getType());

        final PropertyFinder<Tuple3<Long, Integer, Short>> propertyFinder = cm.newPropertyFinder(isValidPropertyMeta);

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

        Assert.assertEquals(6l, fieldA.getGetter().get(tuple).longValue());
        Assert.assertEquals(7, fieldB.getGetter().get(tuple).intValue());
        Assert.assertEquals(3, fieldC.getGetter().get(tuple).shortValue());

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

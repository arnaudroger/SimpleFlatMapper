package org.simpleflatmapper.reflect.test.tuples.jool;

import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ConstructorPropertyMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.test.meta.TestPropertyFinderProbe;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JoolTupleTest {

    private PropertyFinder.PropertyFilter isValidPropertyMeta = PropertyFinder.PropertyFilter.trueFilter();


    @Test
    public void issue488() {

        ClassMeta<List<? extends DbObject>> cm3 =
                ReflectionService.newInstance().getClassMeta(new TypeReference<List<? extends DbObject>>(){}.getType());

        PropertyFinder<List<? extends DbObject>> propertyFinder3 = cm3.newPropertyFinder();

        PropertyMeta<List<? extends DbObject>, Object> prop3 = propertyFinder3.findProperty(DefaultPropertyNameMatcher.of("elt0_id"), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter());

        System.out.println("prop3 = " + prop3.getPath());

        assertEquals("[0].id", prop3.getPath());




        ClassMeta<List<? extends Tuple2<DbObject, Long>>> cm2 =
                ReflectionService.newInstance().getClassMeta(new TypeReference<List<? extends Tuple2<DbObject, Long>>>(){}.getType());

        PropertyFinder<List<? extends Tuple2<DbObject, Long>>> propertyFinder2 = cm2.newPropertyFinder();

        PropertyMeta<List<? extends Tuple2<DbObject, Long>>, Object> prop2 = propertyFinder2.findProperty(DefaultPropertyNameMatcher.of("elt0_elt0_id"), new Object[0], (TypeAffinity)null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());

        System.out.println("prop2 = " + prop2.getPath());

        assertEquals("[0].element0.id", prop2.getPath());



        ClassMeta<Tuple2<String, List<? extends Tuple2<DbObject, Long>>>> cm =
                ReflectionService.newInstance().getClassMeta(new TypeReference<Tuple2<String, List<? extends Tuple2<DbObject, Long>>>>(){}.getType());

        PropertyFinder<Tuple2<String, List<? extends Tuple2<DbObject, Long>>>> propertyFinder = cm.newPropertyFinder();

        PropertyMeta<Tuple2<String, List<? extends Tuple2<DbObject, Long>>>, Object> prop = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("elt1_elt0_elt0_id"), new Object[0], (TypeAffinity)null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());

        System.out.println("prop = " + prop.getPath());
        assertEquals("element1[0].element0.id", prop.getPath());



    }
    
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

        final PropertyMeta<Tuple3<Long, Integer, Short>, Long> fieldA = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt0", 0, true, true), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        final PropertyMeta<Tuple3<Long, Integer, Short>, Integer> fieldB = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt1", 0, true, true), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        final PropertyMeta<Tuple3<Long, Integer, Short>, Short> fieldC = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt2", 0, true, true), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        final PropertyMeta<Tuple3<Long, Integer, Short>, ?> fieldD = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt3", 0, true, true), new Object[0], (TypeAffinity)null, isValidPropertyMeta);

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

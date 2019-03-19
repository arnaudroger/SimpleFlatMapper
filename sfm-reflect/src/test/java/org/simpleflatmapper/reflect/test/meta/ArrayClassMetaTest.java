package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class ArrayClassMetaTest {

    @Test
    public void testArrayObject() throws Exception {
        ClassMeta<?> classMeta = ReflectionService.newInstance().getClassMeta(Object[].class);

        assertTrue(classMeta instanceof ArrayClassMeta);

        Predicate predicate = new Predicate() {
            @Override
            public boolean test(Object propertyMeta) {
                return true;
            }
        };
        PropertyMeta p = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("3"), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter());

        Object[] array = new Object[10];
        p.getSetter().set(array, "aaa");
        assertEquals("aaa", p.getGetter().get(array));
        assertEquals("aaa", array[3]);
    }

    @Test
    public void testArrayPrimitive() throws Exception {
        testArrayPrimitive(boolean.class, true);
        testArrayPrimitive(byte.class, (byte)3);
        testArrayPrimitive(char.class, 'c');
        testArrayPrimitive(short.class, (short)3);
        testArrayPrimitive(int.class, 3);
        testArrayPrimitive(long.class, 3l);
        testArrayPrimitive(float.class, (float)3.3);
        testArrayPrimitive(double.class, (double)3.3);
    }

    private void testArrayPrimitive(Class<?> component, Object value) throws Exception {
        assertTrue(component.isPrimitive());

        Object array = Array.newInstance(component, 10);

        ClassMeta<?> classMeta = ReflectionService.newInstance().getClassMeta(array.getClass());

        assertTrue(classMeta instanceof ArrayClassMeta);

        PropertyMeta p = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("3"), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter());

        p.getSetter().set(array, value);
        assertEquals(value, p.getGetter().get(array));
        assertEquals(value, Array.get(array, 3));
    }

    @Test
    public void testSet() throws Exception {
        TypeReference<Set<String>> typeReference = new TypeReference<Set<String>>() {
        };
        assertTrue(testSet(typeReference) instanceof HashSet);
    }

    @Test
    public void testTreeSet() throws Exception {
        TypeReference<TreeSet<String>> typeReference = new TypeReference<TreeSet<String>>() {
        };
        assertTrue(testSet(typeReference) instanceof TreeSet);
    }

    private Object testSet(TypeReference<?> typeReference) throws Exception {
        ClassMeta<?> classMeta = ReflectionService.newInstance().getClassMeta(typeReference.getType());

        assertTrue(classMeta instanceof ArrayClassMeta);

        PropertyMeta p = findProperty(classMeta);

        Set<String> list = (Set<String>) instantiate(classMeta);
        p.getSetter().set(list, "aaa");

        assertEquals(null, p.getGetter().get(list));
        assertEquals("aaa", list.iterator().next());
        return list;
    }

    @Test
    public void testList() throws Exception {
        TypeReference<List<String>> typeReference = new TypeReference<List<String>>() {
        };
        testGetterSetterOnList(typeReference);
    }

    @Test
    public void testLinkedList() throws Exception {
        TypeReference<LinkedList<String>> typeReference = new TypeReference<LinkedList<String>>() {
        };
        assertTrue(testGetterSetterOnList(typeReference) instanceof LinkedList);
    }


    @Test
    public void testCollection() throws Exception {
        TypeReference<Collection<String>> typeReference = new TypeReference<Collection<String>>() {
        };
        testGetterSetterOnList(typeReference);
    }

    @Test
    public void testIterable() throws Exception {
        TypeReference<Iterable<String>> typeReference = new TypeReference<Iterable<String>>() {
        };
        testGetterSetterOnList(typeReference);
    }

    private Object testGetterSetterOnList(TypeReference<?> typeReference) throws Exception {
        ClassMeta<?> classMeta = ReflectionService.newInstance().getClassMeta(typeReference.getType());

        assertTrue(classMeta instanceof ArrayClassMeta);

        PropertyMeta p = findProperty(classMeta);

        Object list = instantiate(classMeta);
        p.getSetter().set(list, "aaa");

        assertEquals("aaa", p.getGetter().get(list));

        return list;
    }

    private PropertyMeta findProperty(ClassMeta<?> classMeta) {
        return classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("v"), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter());
    }

    private Object instantiate(ClassMeta<?> classMeta) throws Exception {
        InstantiatorDefinition instantiatorDefinition = classMeta.getInstantiatorDefinitions().get(0);
        InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);
        Instantiator instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, (Class)Object.class, new HashMap(), false, true);
        return instantiator.newInstance(null);
    }

}
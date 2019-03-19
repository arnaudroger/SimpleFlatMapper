package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.ConstructorNotFoundException;
import org.simpleflatmapper.reflect.DefaultReflectionService;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.test.junit.LibrarySetsClassLoader;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.tuple.Tuple2;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TupleClassMetaTest {
    private PropertyFinder.PropertyFilter isValidPropertyMeta = PropertyFinder.PropertyFilter.trueFilter();

    @Test
    public void failOnNoConstructorMatchingType() {
        Type type = new TypeReference<MyTuple<String, String>>() {}.getType();

        try {
            new TupleClassMeta<MyTuple<String, String>>(type, ReflectionService.newInstance());
            fail();
        }  catch (ConstructorNotFoundException e) {
            // expect
        }
    }

    @SuppressWarnings("unused")
    static class MyTuple<T1, T2> {
    }

    @Test
    public void testFindPropertyNoAsm() {
        Type type = new TypeReference<Tuple2<String, String>>() {}.getType();

        ClassMeta<Tuple2<String, String>> classMeta = ReflectionService.disableAsm().getClassMeta(type);

        InstantiatorDefinition instantiatorDefinition = classMeta.getInstantiatorDefinitions().get(0);

        assertEquals("element0", instantiatorDefinition.getParameters()[0].getName());
        assertEquals("element1", instantiatorDefinition.getParameters()[1].getName());
        assertEquals(2, instantiatorDefinition.getParameters().length);
    }


    ClassMeta<Tuple2<Foo, Foo>> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<Tuple2<Foo, Foo>>() {}.getType());

    @Test
    public void testIndexStartingAtZero() {
        final PropertyFinder<Tuple2<Foo, Foo>> propertyFinder = classMeta.newPropertyFinder();

        final PropertyMeta<Tuple2<Foo, Foo>, ?> t0_foo = propertyFinder.findProperty(newMatcher("t0_foo"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        final PropertyMeta<Tuple2<Foo, Foo>, ?> t0_bar = propertyFinder.findProperty(newMatcher("t0_bar"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        final PropertyMeta<Tuple2<Foo, Foo>, ?> t1_foo = propertyFinder.findProperty(newMatcher("t1_foo"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        final PropertyMeta<Tuple2<Foo, Foo>, ?> t1_bar = propertyFinder.findProperty(newMatcher("t1_bar"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);

        validate(t0_foo, t0_bar, t1_foo, t1_bar);

    }

    private void validate(PropertyMeta<Tuple2<Foo, Foo>, ?> t0_foo,
                          PropertyMeta<Tuple2<Foo, Foo>, ?> t0_bar,
                          PropertyMeta<Tuple2<Foo, Foo>, ?> t1_foo,
                          PropertyMeta<Tuple2<Foo, Foo>, ?> t1_bar) {

        assertNotNull(t0_foo);
        assertIs("element0", "foo", t0_foo);
        assertNotNull(t0_bar);
        assertIs("element0", "bar", t0_bar);

        assertNotNull(t1_foo);
        assertIs("element1", "foo", t1_foo);
        assertNotNull(t1_foo);
        assertIs("element1", "bar", t1_bar);

    }

    @SuppressWarnings("unchecked")
    private void assertIs(String elementName, String prop, PropertyMeta<Tuple2<Foo, Foo>, ?> propertyMeta) {
        assertTrue(propertyMeta.isSubProperty());
        SubPropertyMeta<Tuple2<Foo, Foo>, Foo, String> subPropertyMeta = (SubPropertyMeta<Tuple2<Foo, Foo>, Foo, String>) propertyMeta;

        assertEquals(elementName, subPropertyMeta.getOwnerProperty().getName());
        assertEquals(prop, subPropertyMeta.getSubProperty().getName());
    }

    private PropertyNameMatcher newMatcher(String name) {
        return new DefaultPropertyNameMatcher(name, 0, false, false);
    }

    @Test
    public void testIndexStartingFlexiblePrefix() {
        final PropertyFinder<Tuple2<Foo, Foo>> propertyFinder = classMeta.newPropertyFinder();

        final PropertyMeta<Tuple2<Foo, Foo>, ?> t0_foo = propertyFinder.findProperty(newMatcher("ta_foo"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        final PropertyMeta<Tuple2<Foo, Foo>, ?> t0_bar = propertyFinder.findProperty(newMatcher("ta_bar"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        final PropertyMeta<Tuple2<Foo, Foo>, ?> t1_foo = propertyFinder.findProperty(newMatcher("tb_foo"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        final PropertyMeta<Tuple2<Foo, Foo>, ?> t1_bar = propertyFinder.findProperty(newMatcher("tb_bar"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        validate(t0_foo, t0_bar, t1_foo, t1_bar);

    }

    @Test
    public void testForEach() {
        final List<String> names = new ArrayList<String>();
        ClassMeta<Object> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<Tuple2<Foo, Foo>>() {
        }.getType());
        classMeta.forEachProperties(new Consumer<PropertyMeta<?, ?>>() {
            @Override
            public void accept(PropertyMeta<?, ?> dbObjectPropertyMeta) {
                names.add(dbObjectPropertyMeta.getName());
            }
        });

        assertEquals(Arrays.asList("element0", "element1"), names);
    }

    @Test
    public void testRespecify() throws ClassNotFoundException, MalformedURLException {
        ClassLoader cl = new URLClassLoader(new URL[] {LibrarySetsClassLoader.findUrl(Tuple2.class, getClass().getClassLoader())}, null) {
            @Override
            public InputStream getResourceAsStream(String name) {
                return null;
            }
        };
        ReflectionService reflectionService = new DefaultReflectionService(null) {
        };

        Class<?> tuple2Class = cl.loadClass(Tuple2.class.getName());

        TupleClassMeta classMeta = new TupleClassMeta(tuple2Class, reflectionService);

        List<InstantiatorDefinition> instantiatorDefinitions = classMeta.getInstantiatorDefinitions();

        assertEquals(1, instantiatorDefinitions.size());
        assertEquals("element0", instantiatorDefinitions.get(0).getParameters()[0].getName());
    }


}

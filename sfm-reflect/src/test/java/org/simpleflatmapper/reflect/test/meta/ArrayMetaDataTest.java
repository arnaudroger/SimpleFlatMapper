package org.simpleflatmapper.reflect.test.meta;


import org.junit.Test;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ArrayMetaDataTest {

    private Predicate<PropertyMeta<?, ?>> isValidPropertyMeta = ConstantPredicate.truePredicate();

    @Test
    public void testListOfString() throws Exception {
        ClassMeta<Object> classMeta = ReflectionService.newInstance()
                .getClassMeta(new TypeReference<List<String>>() {
        }.getType());

        assertTrue(classMeta instanceof ArrayClassMeta);

        // indexed
        PropertyFinder<Object> propertyFinder = classMeta.newPropertyFinder(isValidPropertyMeta);
        PropertyMeta<Object, Object> _0 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("0", 0, false, false));
        ListElementPropertyMeta<Object, Object> meta0 = (ListElementPropertyMeta<Object, Object>) _0;
        assertEquals(0, meta0.getIndex());

        List<String> list = new ArrayList<String>();

        assertNull(meta0.getGetter().get(list));
        meta0.getSetter().set(list, "aa");
        assertEquals("aa", meta0.getGetter().get(list));

        // index discovery
        PropertyMeta<Object, Object> bb = propertyFinder.findProperty(new DefaultPropertyNameMatcher("bb", 0, false, false));

        assertTrue(bb instanceof ListElementPropertyMeta);
        ListElementPropertyMeta<Object, Object> meta = (ListElementPropertyMeta<Object, Object>) bb;
        assertEquals(1, meta.getIndex());



        assertNull(meta.getGetter().get(list));
        meta.getSetter().set(list, "aa");
        assertEquals("aa", meta.getGetter().get(list));

        assertEquals("ListElementPropertyMeta{index=1}", bb.toString());

        assertEquals(ArrayList.class.getConstructor(),
                ((ExecutableInstantiatorDefinition)propertyFinder.getEligibleInstantiatorDefinitions().get(0)).getExecutable());

    }

    @Test
    public void testArrayOfString() throws Exception {
        ClassMeta<String[]> classMeta = ReflectionService.newInstance().getClassMeta(String[].class);

        assertTrue(classMeta instanceof ArrayClassMeta);

        PropertyFinder<String[]> propertyFinder = classMeta.newPropertyFinder(isValidPropertyMeta);
        PropertyMeta<String[], String> bb = propertyFinder.findProperty(new DefaultPropertyNameMatcher("bb", 0, false, false));

        assertTrue("expect ArrayElementPropertyMeta " + bb, bb instanceof ArrayElementPropertyMeta);
        ArrayElementPropertyMeta<String[], String> meta = (ArrayElementPropertyMeta<String[], String>) bb;

        String[] list = new String[2];
        assertNull(meta.getGetter().get(list));
        meta.getSetter().set(list, "aa");
        assertEquals("aa", meta.getGetter().get(list));

        assertEquals("ArrayElementPropertyMeta{index=0}", bb.toString());
        assertTrue(propertyFinder.getEligibleInstantiatorDefinitions().isEmpty());

    }
}

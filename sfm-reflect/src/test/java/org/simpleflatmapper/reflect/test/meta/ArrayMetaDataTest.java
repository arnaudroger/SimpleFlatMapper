package org.simpleflatmapper.reflect.test.meta;


import org.junit.Test;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.reflect.property.SpeculativeArrayIndexResolutionProperty;
import org.simpleflatmapper.util.TypeReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ArrayMetaDataTest {

    private PropertyFinder.PropertyFilter isValidPropertyMeta = PropertyFinder.PropertyFilter.trueFilter();

    @Test
    public void testListOfString() throws Exception {
        ClassMeta<Object> classMeta = ReflectionService.newInstance()
                .getClassMeta(new TypeReference<List<String>>() {
                }.getType());

        assertTrue(classMeta instanceof ArrayClassMeta);

        // indexed
        PropertyFinder<Object> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<Object, Object> _0 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("0", 0, false, false), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        ArrayElementPropertyMeta<Object, Object> meta0 = (ArrayElementPropertyMeta<Object, Object>) _0;
        assertEquals(0, meta0.getIndex());

        List<String> list = new ArrayList<String>();

        assertNull(meta0.getGetter().get(list));
        meta0.getSetter().set(list, "aa");
        assertEquals("aa", meta0.getGetter().get(list));

        // index discovery
        PropertyMeta<Object, Object> bb = propertyFinder.findProperty(new DefaultPropertyNameMatcher("bb", 0, false, false), new Object[] {SpeculativeArrayIndexResolutionProperty.INSTANCE}, (TypeAffinity)null, isValidPropertyMeta).compressSubSelf();

        assertTrue(bb.compressSubSelf() instanceof ArrayElementPropertyMeta);
        ArrayElementPropertyMeta<Object, Object> meta = (ArrayElementPropertyMeta<Object, Object>) bb.compressSubSelf();
        assertEquals(1, meta.getIndex());



        assertNull(meta.getGetter().get(list));
        meta.getSetter().set(list, "aa");
        assertEquals("aa", meta.getGetter().get(list));

        assertEquals("ArrayElementPropertyMeta{index=1}", bb.toString());

        assertEquals(ArrayList.class.getConstructor(),
                ((ExecutableInstantiatorDefinition)propertyFinder.getEligibleInstantiatorDefinitions().get(0)).getExecutable());

    }


    @Test
    public void testSetOfString() throws Exception {
        ClassMeta<Object> classMeta = ReflectionService.newInstance()
                .getClassMeta(new TypeReference<Set<String>>() {
                }.getType());

        assertTrue(classMeta instanceof ArrayClassMeta);

        // indexed
        PropertyFinder<Object> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<Object, Object> _0 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("0", 0, false, false), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        ArrayElementPropertyMeta<Object, Object> meta0 = (ArrayElementPropertyMeta<Object, Object>) _0;
        assertEquals(0, meta0.getIndex());

        Set<String> list = new HashSet<String>();

        assertNull(meta0.getGetter().get(list));
        meta0.getSetter().set(list, "aa");
        assertEquals("aa", list.iterator().next());
        assertNull(meta0.getGetter().get(list));


        assertEquals(HashSet.class.getConstructor(),
                ((ExecutableInstantiatorDefinition)propertyFinder.getEligibleInstantiatorDefinitions().get(0)).getExecutable());

    }

    @Test
    public void testArrayOfString() throws Exception {
        ClassMeta<String[]> classMeta = ReflectionService.newInstance().getClassMeta(String[].class);

        assertTrue(classMeta instanceof ArrayClassMeta);

        PropertyFinder<String[]> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<String[], ?> bb = propertyFinder.findProperty(new DefaultPropertyNameMatcher("bb", 0, false, false), new Object[0], (TypeAffinity)null, isValidPropertyMeta).compressSubSelf();

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

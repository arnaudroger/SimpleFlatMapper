package org.sfm.reflect.meta;


import org.junit.Test;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ArrayMetaDataTest {

    @Test
    public void testListOfString() throws Exception {
        ClassMeta<Object> classMeta = ReflectionService.newInstance().getRootClassMeta(new TypeReference<List<String>>() {
        }.getType());

        assertTrue(classMeta instanceof ArrayClassMeta);

        // indexed
        PropertyFinder<Object> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<Object, Object> _0 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("0", 0, false, false));
        ListElementPropertyMeta<Object, Object> meta0 = (ListElementPropertyMeta<Object, Object>) _0;
        assertEquals(0, meta0.getIndex());

        List<String> list = new ArrayList<String>();

        assertNull(meta0.newGetter().get(list));
        meta0.newSetter().set(list, "aa");
        assertEquals("aa", meta0.newGetter().get(list));

        // index discovery
        PropertyMeta<Object, Object> bb = propertyFinder.findProperty(new DefaultPropertyNameMatcher("bb", 0, false, false));

        assertTrue(bb instanceof ListElementPropertyMeta);
        ListElementPropertyMeta<Object, Object> meta = (ListElementPropertyMeta<Object, Object>) bb;

        assertEquals(1, meta.getIndex());



        assertNull(meta.newGetter().get(list));
        meta.newSetter().set(list, "aa");
        assertEquals("aa", meta.newGetter().get(list));

        assertEquals("ListElementPropertyMeta{index=1}", bb.toString());

        assertEquals(ArrayList.class.getConstructor(), propertyFinder.getEligibleConstructorDefinitions().get(0).getConstructor());
        assertNull(propertyFinder.findConstructor(null));

        try {
            classMeta.generateHeaders();
            fail();
        } catch(Exception e) {}
    }

    @Test
    public void testArrayOfString() throws Exception {
        ClassMeta<String[]> classMeta = ReflectionService.newInstance().getRootClassMeta(String[].class);

        assertTrue(classMeta instanceof ArrayClassMeta);

        PropertyFinder<String[]> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<String[], String> bb = propertyFinder.findProperty(new DefaultPropertyNameMatcher("bb", 0, false, false));

        assertTrue(bb instanceof ArrayElementPropertyMeta);
        ArrayElementPropertyMeta<String[], String> meta = (ArrayElementPropertyMeta<String[], String>) bb;

        String[] list = new String[2];
        assertNull(meta.newGetter().get(list));
        meta.newSetter().set(list, "aa");
        assertEquals("aa", meta.newGetter().get(list));

        assertEquals("ArrayElementPropertyMeta{index=0}", bb.toString());
        assertTrue(propertyFinder.getEligibleConstructorDefinitions().isEmpty());

    }
}

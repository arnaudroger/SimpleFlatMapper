package org.sfm.reflect.meta;


import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

public class MapClassMetaTest {


    @Test
    public void testFindPropertyStringDbObject() {
        final ClassMeta<Map<String, DbObject>> classMeta =
                ReflectionService.newInstance().getClassMeta(new TypeReference<Map<String, DbObject>>() {}.getType());
        final PropertyFinder<Map<String, DbObject>> mapPropertyFinder = classMeta.newPropertyFinder();

        assertNull(mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_noprop")));
        final SubPropertyMeta<?, ?, ?> k_kv_k_id =
                (SubPropertyMeta<?, ?, ?>) mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_id"));
        assertNotNull(k_kv_k_id);
        MapElementPropertyMeta<?, ?, ?> idMeta = (MapElementPropertyMeta<?, ?, ?>) k_kv_k_id.getOwnerProperty();
        assertEquals("k_kv_k", idMeta.getKey());

        final SubPropertyMeta<Map<String, DbObject>, DbObject, Object> k_kv_k_creation_time = (SubPropertyMeta<Map<String, DbObject>, DbObject, Object>) mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_creation_time"));
        assertNotNull(k_kv_k_creation_time);
        MapElementPropertyMeta<?, ?, ?> creationTimeMeta = (MapElementPropertyMeta<?, ?, ?>) k_kv_k_creation_time.getOwnerProperty();
        assertEquals("k_kv_k", creationTimeMeta.getKey());

    }

    @Test
    public void testFindPropertyStringString() {
        final ClassMeta<Map<String, String>> classMeta =
                ReflectionService.newInstance().getClassMeta(new TypeReference<Map<String, String>>() {}.getType());
        final PropertyFinder<Map<String, String>> mapPropertyFinder = classMeta.newPropertyFinder();

        final PropertyMeta<?, ?> k_kv_k_id = mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_id"));
        assertNotNull(k_kv_k_id);
        MapElementPropertyMeta<?, ?, ?> idMeta = (MapElementPropertyMeta<?, ?, ?>) k_kv_k_id;
        assertEquals("k_kv_k_id", idMeta.getKey());


    }

    @Test
    public void testFailOnAbstractMap() throws Exception {
        try {
            ReflectionService.newInstance().getClassMeta(new TypeReference<AbstractMap<String, String>>() {}.getType());
            fail();
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testFailOnAbstractOnImplWithNoEmptyConstructor() throws Exception {
        try {
            ReflectionService.newInstance().getClassMeta(NoEmptyConstructorMap.class);
            fail();
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testFailOnUnsupportedConverter() throws Exception {
        try {
            ReflectionService.newInstance().getClassMeta(new TypeReference<Map<Map<String, String>, String>>() {}.getType());
            fail();
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testUseHashMapOnMap() throws NoSuchMethodException {
        typeMetaHasTheSpecifiedClassEmptyConstructor(
                new TypeReference<Map<String, String>>() {},
                HashMap.class);

    }

    @Test
    public void testUseConcurrentHashMapOnConcurrentMap() throws NoSuchMethodException {
        typeMetaHasTheSpecifiedClassEmptyConstructor(
                new TypeReference<ConcurrentMap<String, String>>() {},
                ConcurrentHashMap.class);
    }

    @Test
    public void testUseSpecifiedImplType() throws NoSuchMethodException {
        typeMetaHasTheSpecifiedClassEmptyConstructor(
                new TypeReference<MyHashMap>() {},
                MyHashMap.class);
    }

    static class MyHashMap extends HashMap<String, String> {

    }



    private void typeMetaHasTheSpecifiedClassEmptyConstructor(TypeReference<?> typeReference, Class<?> impl) throws NoSuchMethodException {
        final ClassMeta<Map<String, String>> classMeta =
                ReflectionService.newInstance().getClassMeta(typeReference.getType());

        assertEquals(typeReference.getType(), classMeta.getType());
        assertFalse(classMeta.isLeaf());

        try {
            classMeta.generateHeaders();
            fail();
        }  catch (UnsupportedOperationException e) {
        }

        hasOneInstantiatorDefinitionWithEmptyConstructorOnImpl(classMeta, impl);
    }



    private void hasOneInstantiatorDefinitionWithEmptyConstructorOnImpl(ClassMeta<?> classMeta, Class<?> impl) throws NoSuchMethodException {
        assertTrue(classMeta instanceof MapClassMeta);
        final List<InstantiatorDefinition> instantiatorDefinitions = classMeta.getInstantiatorDefinitions();

        assertEquals(1, instantiatorDefinitions.size());
        final InstantiatorDefinition instantiatorDefinition = instantiatorDefinitions.get(0);

        assertEquals(0, instantiatorDefinition.getParameters().length);
        assertEquals(impl.getDeclaredConstructor(), instantiatorDefinition.getExecutable());
    }


    static class NoEmptyConstructorMap extends HashMap<String, String> {
        public NoEmptyConstructorMap(int i) {
        }
    }
}

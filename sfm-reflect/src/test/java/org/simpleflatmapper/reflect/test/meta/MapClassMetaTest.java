package org.simpleflatmapper.reflect.test.meta;


import org.junit.Test;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

public class MapClassMetaTest {
    private PropertyFinder.PropertyFilter isValidPropertyMeta = PropertyFinder.PropertyFilter.trueFilter();

    @Test
    public void testForEach() {
        try {
            ReflectionService.newInstance().getClassMeta(new TypeReference<Map<String, String>>() {}.getType()).forEachProperties(new Consumer<PropertyMeta<?, ?>>() {
                @Override
                public void accept(PropertyMeta<?, ?> dbObjectPropertyMeta) {
                }
            });
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
    
    @Test
    public void testUntypeMap494() {
        MapClassMeta<?, ?, ?> classMeta = (MapClassMeta<?, ?, ?> )ReflectionService.newInstance().getClassMeta(MyMap.class);
        assertNotNull(classMeta);
    }
    @Test
    public void testUntypeList494() {
        ArrayClassMeta<?, ?> classMeta = (ArrayClassMeta<?, ?> )ReflectionService.newInstance().getClassMeta(MyList.class);
        assertNotNull(classMeta);
    }
    public static class MyMap extends HashMap {
        
    }
    
    public static class MyList extends ArrayList {
        
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testFindPropertyStringDbObject() {
        final ClassMeta<Map<String, DbObject>> classMeta =
                ReflectionService.newInstance().getClassMeta(new TypeReference<Map<String, DbObject>>() {}.getType());
        final PropertyFinder<Map<String, DbObject>> mapPropertyFinder = classMeta.newPropertyFinder();

        final SubPropertyMeta<?, ?, ?> k_kv_k_id =
                (SubPropertyMeta<?, ?, ?>) mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_id"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(k_kv_k_id);

        PropertyMeta<Map<String, DbObject>, Object> k_kv_k_noprop = mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_noprop"), new Object[0], (TypeAffinity)null, isValidPropertyMeta).compressSubSelf();
        assertTrue(k_kv_k_noprop instanceof MapElementPropertyMeta); // self ref

        MapElementPropertyMeta<?, ?, ?> idMeta = (MapElementPropertyMeta<?, ?, ?>) k_kv_k_id.getOwnerProperty();
        assertEquals("k_kv_k", idMeta.getKey());

        final SubPropertyMeta<Map<String, DbObject>, DbObject, Object> k_kv_k_creation_time = (SubPropertyMeta<Map<String, DbObject>, DbObject, Object>) mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_creation_time"), new Object[0], (TypeAffinity) null, isValidPropertyMeta);
        assertNotNull(k_kv_k_creation_time);
        MapElementPropertyMeta<?, ?, ?> creationTimeMeta = (MapElementPropertyMeta<?, ?, ?>) k_kv_k_creation_time.getOwnerProperty();
        assertEquals("k_kv_k", creationTimeMeta.getKey());
            
    }

    @Test
    public void testFindPropertyStringString() {
        final ClassMeta<Map<String, String>> classMeta =
                ReflectionService.newInstance().getClassMeta(new TypeReference<Map<String, String>>() {}.getType());
        final PropertyFinder<Map<String, String>> mapPropertyFinder = classMeta.newPropertyFinder();

        final PropertyMeta<?, ?> k_kv_k_id = mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_id"), new Object[0], (TypeAffinity)null, isValidPropertyMeta).compressSubSelf();
        assertNotNull(k_kv_k_id);
        assertTrue("Expect MapElementPropertyMeta " + k_kv_k_id, k_kv_k_id instanceof  MapElementPropertyMeta);
        MapElementPropertyMeta<?, ?, ?> idMeta = (MapElementPropertyMeta<?, ?, ?>) k_kv_k_id;
        assertEquals("k_kv_k_id", idMeta.getKey());


    }

    @Test
    public void testNotFailOnAbstractMap() throws Exception {
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
    public void testNotFailOnUnsupportedConverter() throws Exception {
        ReflectionService.newInstance().getClassMeta(new TypeReference<Map<Map<String, String>, String>>() {}.getType());
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

        hasOneInstantiatorDefinitionWithEmptyConstructorOnImpl(classMeta, impl);
    }



    private void hasOneInstantiatorDefinitionWithEmptyConstructorOnImpl(ClassMeta<?> classMeta, Class<?> impl) throws NoSuchMethodException {
        assertTrue(classMeta instanceof MapClassMeta);
        final List<InstantiatorDefinition> instantiatorDefinitions = classMeta.getInstantiatorDefinitions();

        assertEquals(1, instantiatorDefinitions.size());
        final ExecutableInstantiatorDefinition instantiatorDefinition = (ExecutableInstantiatorDefinition) instantiatorDefinitions.get(0);

        assertEquals(0, instantiatorDefinition.getParameters().length);
        assertEquals(impl.getDeclaredConstructor(), instantiatorDefinition.getExecutable());
    }


    static class NoEmptyConstructorMap extends HashMap<String, String> {
        public NoEmptyConstructorMap(int i) {
        }
    }
}

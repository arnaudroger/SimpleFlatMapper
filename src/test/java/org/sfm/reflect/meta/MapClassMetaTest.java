package org.sfm.reflect.meta;


import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MapClassMetaTest {


    @Test
    public void testFindPropertyStringDbObject() {
        final ClassMeta<Map<String, DbObject>> classMeta =
                ReflectionService.newInstance().getClassMeta(new TypeReference<Map<String, DbObject>>() {}.getType());
        final PropertyFinder<Map<String, DbObject>> mapPropertyFinder = classMeta.newPropertyFinder();

        assertNull(mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_noprop")));
        final SubPropertyMeta<?, ?> k_kv_k_id =
                (SubPropertyMeta<?, ?>) mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_id"));
        assertNotNull(k_kv_k_id);
        MapElementPropertyMeta<?, ?, ?> idMeta = (MapElementPropertyMeta<?, ?, ?>) k_kv_k_id.getOwnerProperty();
        assertEquals("k_kv_k", idMeta.getKey());

        final SubPropertyMeta<Map<String, DbObject>, Object> k_kv_k_creation_time = (SubPropertyMeta<Map<String, DbObject>, Object>) mapPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("k_kv_k_creation_time"));
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
}

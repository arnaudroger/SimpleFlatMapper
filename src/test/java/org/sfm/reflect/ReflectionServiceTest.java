package org.sfm.reflect;

import org.junit.Test;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.tuples.Tuple2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class ReflectionServiceTest {


    @Test
    public void testClassMetaCache() {
        final ReflectionService reflectionService = ReflectionService.newInstance();

        assertSame(
                reflectionService.getClassMeta(new TypeReference<Tuple2<String, String>>() {}.getType()),
                reflectionService.getClassMeta(new TypeReference<Tuple2<String, String>>() {}.getType())
        );
        assertNotSame(
                reflectionService.getClassMeta(new TypeReference<Tuple2<String, String>>() {
                }.getType()),
                reflectionService.getClassMeta(new TypeReference<Tuple2<String, Long>>() {
                }.getType())
        );
    }


    @Test
    public void testSelfReferringClass() {
        final ReflectionService reflectionService = ReflectionService.newInstance();

        ClassMeta<Node> cm = reflectionService.getClassMeta(Node.class);

        final PropertyMeta<Node, Object> propertyMeta = cm.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("parent_parent_parent"));
        assertNotNull(propertyMeta);
        assertNotNull(cm);
    }

    public static class Node {
        public Node parent;
    }
}

package org.simpleflatmapper.reflect.test.setter;

import org.junit.Test;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.reflect.setter.AppendCollectionSetter;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Issue451Test {
    
    @Test
    public void testPropertyFinderAndAppendSetter() {

        ClassMeta<List<Foo>> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<List<Foo>>() {
        }.getType());

        PropertyFinder.PropertyFilter predicate = PropertyFinder.PropertyFilter.trueFilter();

        PropertyFinder<List<Foo>> finder = classMeta.newPropertyFinder();

        SubPropertyMeta f = (SubPropertyMeta)finder.findProperty(DefaultPropertyNameMatcher.of("b_f"), new Object[0], (TypeAffinity)null, predicate);
        assertNotNull(f);
        SubPropertyMeta n = (SubPropertyMeta)finder.findProperty(DefaultPropertyNameMatcher.of("b_n"), new Object[0], (TypeAffinity)null, predicate);

        assertEquals(AppendCollectionSetter.class, n.getOwnerProperty().getSetter().getClass());

    }
    
    
    public static class Foo {
        public int f;
        public String n;
    }
}

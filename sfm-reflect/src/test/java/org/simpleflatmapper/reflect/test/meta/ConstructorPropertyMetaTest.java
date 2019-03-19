package org.simpleflatmapper.reflect.test.meta;


import org.junit.Test;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.setter.NullSetter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConstructorPropertyMetaTest {

    private PropertyFinder.PropertyFilter isValidPropertyMeta = PropertyFinder.PropertyFilter.trueFilter();

    ClassMeta<CObject> classMeta = ReflectionService.newInstance().getClassMeta(CObject.class);
    CObject cObject = new CObject("v1", "v2");

    @Test
    public void testSetterIsNullSetter() throws Exception {
        PropertyMeta<CObject, Object> property = classMeta.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("p1", 0, false, false), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertTrue(property.getSetter() instanceof NullSetter);
    }

    @Test
    public void testGetValueIfGetterAvailable() throws Exception {
        PropertyMeta<CObject, Object> property = classMeta.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("p1", 0, false, false), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertEquals("v1", property.getGetter().get(cObject));
    }

    @Test
    public void testToString() {
        PropertyMeta<CObject, Object> property = classMeta.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("p1", 0, false, false), new Object[0], (TypeAffinity)null, isValidPropertyMeta);

        assertTrue(property.toString().startsWith("ConstructorPropertyMeta"));
    }


    public static class CObject {
        private final String p1;
        private final String p2;


        public CObject(String p1, String p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        public String getP1() {
            return p1;
        }
    }
}

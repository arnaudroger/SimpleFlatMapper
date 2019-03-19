package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.getter.IdentityGetter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Predicate;

import static org.junit.Assert.*;

public class SelfPropertyMetaTest {

    @Test
    public void testDirect() {
        ClassMeta<String> direct = ReflectionService.newInstance().getClassMeta(String.class);


        PropertyMeta<String, Object> property = direct.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("bbb", 0, true, true), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter());


        assertTrue("Expect SelfPropertyMeta " + property, property instanceof SelfPropertyMeta);
        assertEquals("SelfPropertyMeta{type=class java.lang.String,name=self}", property.toString());

        assertTrue(property.getGetter() instanceof IdentityGetter);

        assertTrue(NullSetter.isNull(property.getSetter()));

        assertEquals("{this}", property.getPath());

        assertEquals(String.class, direct.getType());

    }
}

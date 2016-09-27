package org.simpleflatmapper.reflect.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.getter.IdentityGetter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.setter.NullSetter;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DirectClassMetaTest {

    @Test
    public void testDirect() {
        ClassMeta<String> direct = ReflectionService.newInstance().getClassMeta(String.class);

        PropertyMeta<String, Object> property = direct.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("xyz", 0, true, true));

        assertTrue("Expect SelfPropertyMeta " + property, property instanceof SelfPropertyMeta);
        assertEquals("SelfPropertyMeta{type=class java.lang.String,name=self}", property.toString());

        assertTrue(property.getGetter() instanceof IdentityGetter);

        assertTrue(NullSetter.isNull(property.getSetter()));

        assertEquals("{this}", property.getPath());

        assertEquals(String.class, direct.getType());

    }
}

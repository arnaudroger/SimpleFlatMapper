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

        assertTrue(direct instanceof DirectClassMeta);
        assertEquals("DirectClassMeta{target=class java.lang.String}", direct.toString());

        PropertyMeta<String, Object> property = direct.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("bbb", 0, true, true));

        assertNull(direct.newPropertyFinder().getEligibleInstantiatorDefinitions());

        assertTrue(property instanceof DirectClassMeta.DirectPropertyMeta);
        assertEquals("DirectPropertyMeta{type=class java.lang.String,name=direct}", property.toString());

        assertTrue(property.getGetter() instanceof IdentityGetter);

        assertTrue(NullSetter.isNull(property.getSetter()));

        assertArrayEquals(new String[] {""},  direct.generateHeaders());

        assertEquals(".", property.getPath());

        assertEquals(Arrays.asList(), direct.getInstantiatorDefinitions());
        assertEquals(String.class, direct.getType());

        assertEquals(direct, direct);
        assertNotEquals(direct, ReflectionService.newInstance().getClassMeta(Integer.class));
        assertEquals(direct.hashCode(), direct.hashCode());
        assertNotEquals(direct.hashCode(), ReflectionService.newInstance().getClassMeta(Integer.class).hashCode());


    }
}

package org.sfm.reflect.meta;

import org.junit.Test;
import org.sfm.reflect.ReflectionService;

import static org.junit.Assert.*;

public class DirectClassMetaTest {

    @Test
    public void testDirect() {
        ClassMeta<String> direct = ReflectionService.newInstance().getRootClassMeta(String.class);

        assertTrue(direct instanceof DirectClassMeta);
        assertEquals("DirectClassMeta{target=class java.lang.String}", direct.toString());

        PropertyMeta<String, Object> property = direct.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("bbb", 0, true, true));

        assertNull(direct.newPropertyFinder().findConstructor(null));
        assertNull(direct.newPropertyFinder().getEligibleInstantiatorDefinitions());

        assertTrue(property instanceof DirectClassMeta.DirectPropertyMeta);
        assertEquals("DirectPropertyMeta{type=class java.lang.String}", property.toString());

        try {
            property.newGetter();
            fail("expected exception");
        } catch(Exception e) {
            // expected
        }

        try {
            property.newSetter();
            fail("expected exception");
        } catch(Exception e) {
            // expected
        }

        try {
            direct.generateHeaders();
            fail("expected exception");
        } catch(Exception e) {
            // expected
        }

        assertEquals("direct", property.getPath());

    }
}

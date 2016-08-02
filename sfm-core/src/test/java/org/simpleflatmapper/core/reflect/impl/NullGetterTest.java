package org.simpleflatmapper.core.reflect.impl;

import org.junit.Test;
import org.simpleflatmapper.core.reflect.NullGetter;

import static org.junit.Assert.*;

        public class NullGetterTest {

    @Test
    public void testToString() throws Exception {
        assertEquals("NullGetter{}", NullGetter.<Object, Object>getter().toString());
    }

    @Test
    public void testGet() throws Exception {
        assertNull(NullGetter.<Object, Object>getter().get(null));
    }
}
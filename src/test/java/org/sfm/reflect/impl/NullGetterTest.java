package org.sfm.reflect.impl;

import org.junit.Test;

import static org.junit.Assert.*;

        public class NullGetterTest {

    @Test
    public void testToString() throws Exception {
        assertEquals("NullGetter{}", new NullGetter<Object, Object>().toString());
    }

    @Test
    public void testGet() throws Exception {
        assertNull(new NullGetter<Object, Object>().get(null));
    }
}
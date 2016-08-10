package org.simpleflatmapper.reflect.getter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NullGetterTest {

    @Test
    public void testNullGetter() {
        assertTrue(NullGetter.isNull(null));
        assertTrue(NullGetter.isNull(NullGetter.getter()));
        assertFalse(NullGetter.isNull(new ConstantGetter<Object, Object>(null)));
        assertNull(NullGetter.getter().get(null));

        assertEquals("NullGetter{}", NullGetter.getter().toString());
    }
}

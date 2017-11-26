package org.simpleflatmapper.reflect.test.getter;

import org.junit.Test;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.getter.NullGetter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NullGetterTest {

    @Test
    public void testNullGetter() throws Exception {
        assertTrue(NullGetter.isNull(null));
        assertTrue(NullGetter.isNull(NullGetter.getter()));
        assertFalse(NullGetter.isNull(new ConstantGetter<Object, Object>(null)));
        assertNull(NullGetter.getter().get(null));

        assertEquals("NullGetter{}", NullGetter.getter().toString());
    }
}

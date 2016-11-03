package org.simpleflatmapper.reflect.test.setter;

import org.junit.Test;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.setter.NullSetter;

import static org.junit.Assert.*;

public class NullSetterTest {

    @Test
    public void test() {
        assertTrue(NullSetter.isNull(null));
        assertTrue(NullSetter.isNull(NullSetter.NULL_SETTER));
        assertFalse(NullSetter.isNull(new Setter<Object, Object>() {
            @Override
            public void set(Object target, Object value) throws Exception {
            }
        }));


        NullSetter.NULL_SETTER.set(null, null);

        assertEquals("NullSetter{}", NullSetter.NULL_SETTER.toString());

    }

}
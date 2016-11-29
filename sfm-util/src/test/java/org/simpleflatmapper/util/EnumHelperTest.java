package org.simpleflatmapper.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class EnumHelperTest {


    @Test
    public void testGetValues() {
        assertArrayEquals(E.values(), EnumHelper.getValues(E.class));
    }

    public enum E {
        A, B, C
    }

    @Test
    public void testGextValuesOnNonEnum() {
        try {
            Enum[] values = EnumHelper.getValues((Class) Object.class);
            fail();
        } catch (Error e) {
            // expected
        }
    }
}
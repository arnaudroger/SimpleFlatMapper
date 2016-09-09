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

}
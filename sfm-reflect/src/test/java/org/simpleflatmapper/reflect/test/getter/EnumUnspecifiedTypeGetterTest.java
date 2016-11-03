package org.simpleflatmapper.reflect.test.getter;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.EnumUnspecifiedTypeGetter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnumUnspecifiedTypeGetterTest {

    public enum ENUM { ZERO, ONE, TWO, THREE };

    @Test
    public void testName() throws Exception {
        @SuppressWarnings("unchecked") Getter<Object, Object> getter = mock(Getter.class);
        EnumUnspecifiedTypeGetter<Object, ENUM> eGetter =
                new EnumUnspecifiedTypeGetter<Object, ENUM>(getter, ENUM.class);

        when(getter.get(any())).thenReturn("ZERO", "ONE", "TWO", "THREE");

        assertEquals(ENUM.ZERO, eGetter.get(null));
        assertEquals(ENUM.ONE, eGetter.get(null));
        assertEquals(ENUM.TWO, eGetter.get(null));
        assertEquals(ENUM.THREE, eGetter.get(null));

        eGetter.toString();


    }

    @Test
    public void testOrdinal() throws Exception {

        @SuppressWarnings("unchecked") Getter<Object, Object> getter = mock(Getter.class);
        EnumUnspecifiedTypeGetter<Object, ENUM> eGetter =
                new EnumUnspecifiedTypeGetter<Object, ENUM>(getter, ENUM.class);

        when(getter.get(any())).thenReturn(0, 1, 2, 3);

        assertEquals(ENUM.ZERO, eGetter.get(null));
        assertEquals(ENUM.ONE, eGetter.get(null));
        assertEquals(ENUM.TWO, eGetter.get(null));
        assertEquals(ENUM.THREE, eGetter.get(null));

    }
}
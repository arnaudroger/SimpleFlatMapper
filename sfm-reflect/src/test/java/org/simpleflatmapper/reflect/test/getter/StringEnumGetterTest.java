package org.simpleflatmapper.reflect.test.getter;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.StringEnumGetter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StringEnumGetterTest {


    public enum ENUM { ZERO, ONE, TWO, THREE }

    @Test
    public void test() throws Exception {
        @SuppressWarnings("unchecked") Getter<Object, String> stringGetter = mock(Getter.class);
        StringEnumGetter<Object, ENUM> getter = new StringEnumGetter<Object, ENUM>(stringGetter, ENUM.class);

        when(stringGetter.get(any())).thenReturn("ZERO", "ONE", "TWO", "THREE");

        assertEquals(ENUM.ZERO, getter.get(null));
        assertEquals(ENUM.ONE, getter.get(null));
        assertEquals(ENUM.TWO, getter.get(null));
        assertEquals(ENUM.THREE, getter.get(null));

        getter.toString();
    }



}
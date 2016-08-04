package org.simpleflatmapper.core.reflect.getter;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayIndexedGetterTest {

    @Test
    public void test() {
        ArrayIndexedGetter<String> getter = new ArrayIndexedGetter<String>();

        String[] values = {"hello", "bye"};
        assertEquals("hello", getter.get(values, 0));
        assertEquals("bye", getter.get(values, 1));

        getter.toString();
    }
}
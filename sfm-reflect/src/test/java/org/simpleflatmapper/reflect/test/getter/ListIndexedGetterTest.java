package org.simpleflatmapper.reflect.test.getter;

import org.junit.Test;
import org.simpleflatmapper.reflect.getter.ListIndexedGetter;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ListIndexedGetterTest {

    @Test
    public void test() {
        ListIndexedGetter<String> getter = new ListIndexedGetter<String>();
        List<String>  list = Arrays.asList("str1", "str2", "str3");

        assertEquals("str1", getter.get(list, 0));
        assertEquals("str2", getter.get(list, 1));
        assertEquals("str3", getter.get(list, 2));

        getter.toString();

    }
}
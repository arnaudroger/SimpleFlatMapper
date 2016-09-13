package org.simpleflatmapper.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ListCollectorTest {

    @Test
    public void test() {
        ListCollector<String> handler = new ListCollector<String>();

        handler.accept("str1");
        handler.accept("str2");

        assertEquals(Arrays.asList("str1", "str2"), handler.getList());
    }

}
package org.simpleflatmapper.core.utils;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ListCollectorHandlerTest {

    @Test
    public void test() {
        ListCollectorHandler<String> handler = new ListCollectorHandler<String>();

        handler.handle("str1");
        handler.handle("str2");

        assertEquals(Arrays.asList("str1", "str2"), handler.getList());
    }

}
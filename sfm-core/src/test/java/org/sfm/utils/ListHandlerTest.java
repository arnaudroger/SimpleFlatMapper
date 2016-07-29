package org.sfm.utils;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ListHandlerTest {

    @Test
    public void testList() {
        ListHandler<String> handler = new ListHandler<String>();
        handler.handle("1");
        assertEquals(Arrays.asList("1"), handler.getList());
    }
}
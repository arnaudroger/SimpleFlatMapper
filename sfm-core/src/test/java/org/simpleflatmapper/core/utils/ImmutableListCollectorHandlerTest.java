package org.simpleflatmapper.core.utils;

import org.junit.Test;
import org.simpleflatmapper.core.utils.ImmutableListCollectorHandler;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ImmutableListCollectorHandlerTest {

    @Test
    public void testList() {
        ImmutableListCollectorHandler<String> handler = new ImmutableListCollectorHandler<String>();
        handler.handle("1");
        assertEquals(Arrays.asList("1"), handler.getList());

        try {
            handler.getList().add("1");
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }
}
package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ImmutableListCollector;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ImmutableListCollectorTest {

    @Test
    public void testList() {
        ImmutableListCollector<String> handler = new ImmutableListCollector<String>();
        handler.accept("1");
        assertEquals(Arrays.asList("1"), handler.getList());

        try {
            handler.getList().add("1");
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }
}
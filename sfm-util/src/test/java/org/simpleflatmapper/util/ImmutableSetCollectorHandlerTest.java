package org.simpleflatmapper.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;


public class ImmutableSetCollectorHandlerTest {

    @Test
    public void testCreateSet() {
        ImmutableSetCollectorHandler<String> collectorHandler = new ImmutableSetCollectorHandler<String>();

        collectorHandler.accept("1");
        collectorHandler.accept("2");
        collectorHandler.accept("3");

        assertEquals(new HashSet<String>(Arrays.asList("1", "2", "3")), collectorHandler.getSet());

        try {
            collectorHandler.getSet().add("3");
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

}
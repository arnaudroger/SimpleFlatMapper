package org.sfm.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;


public class ImmutableSetCollectorHandlerTest {

    @Test
    public void testCreateSet() {
        ImmutableSetCollectorHandler<String> collectorHandler = new ImmutableSetCollectorHandler<String>();

        collectorHandler.handle("1");
        collectorHandler.handle("2");
        collectorHandler.handle("3");

        assertEquals(new HashSet<String>(Arrays.asList("1", "2", "3")), collectorHandler.getSet());

        try {
            collectorHandler.getSet().add("3");
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

}
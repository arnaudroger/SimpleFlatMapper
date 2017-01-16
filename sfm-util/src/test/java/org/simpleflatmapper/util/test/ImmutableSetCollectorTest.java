package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ImmutableSetCollector;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;


public class ImmutableSetCollectorTest {

    @Test
    public void testCreateSet() {
        ImmutableSetCollector<String> collectorHandler = new ImmutableSetCollector<String>();

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
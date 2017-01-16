package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.SetCollector;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class SetCollectorHandlerTest {


    @Test
    public void testCreateSet() {
        SetCollector<String> collectorHandler = new SetCollector<String>();

        collectorHandler.accept("1");
        collectorHandler.accept("2");
        collectorHandler.accept("3");

        assertEquals(new HashSet<String>(Arrays.asList("1", "2", "3")), collectorHandler.getSet());
    }
}
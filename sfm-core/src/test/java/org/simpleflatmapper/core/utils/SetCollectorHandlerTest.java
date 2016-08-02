package org.simpleflatmapper.core.utils;

import org.junit.Test;
import org.simpleflatmapper.core.utils.SetCollectorHandler;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class SetCollectorHandlerTest {


    @Test
    public void testCreateSet() {
        SetCollectorHandler<String> collectorHandler = new SetCollectorHandler<String>();

        collectorHandler.handle("1");
        collectorHandler.handle("2");
        collectorHandler.handle("3");

        assertEquals(new HashSet<String>(Arrays.asList("1", "2", "3")), collectorHandler.getSet());
    }
}
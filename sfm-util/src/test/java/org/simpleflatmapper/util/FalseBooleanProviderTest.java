package org.simpleflatmapper.util;

import org.junit.Test;

import static org.junit.Assert.*;


public class FalseBooleanProviderTest {

    @Test
    public void testGetBoolean() throws Exception {
        assertFalse(new FalseBooleanProvider().getBoolean());
    }
}
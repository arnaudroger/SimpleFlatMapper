package org.simpleflatmapper.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class TrueBooleanProviderTest {

    @Test
    public void testGetBoolean() throws Exception {
        assertTrue(new TrueBooleanProvider().getBoolean());
    }
}
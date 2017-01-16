package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.FalseBooleanProvider;

import static org.junit.Assert.*;


public class FalseBooleanProviderTest {

    @Test
    public void testGetBoolean() throws Exception {
        assertFalse(new FalseBooleanProvider().getBoolean());
    }
}
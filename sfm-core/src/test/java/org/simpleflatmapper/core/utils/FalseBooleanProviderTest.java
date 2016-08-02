package org.simpleflatmapper.core.utils;

import org.junit.Test;
import org.simpleflatmapper.core.utils.FalseBooleanProvider;

import static org.junit.Assert.*;


public class FalseBooleanProviderTest {

    @Test
    public void testGetBoolean() throws Exception {
        assertFalse(new FalseBooleanProvider().getBoolean());
    }
}
package org.simpleflatmapper.core.utils;

import org.junit.Test;
import org.simpleflatmapper.core.utils.TrueBooleanProvider;

import static org.junit.Assert.*;

public class TrueBooleanProviderTest {

    @Test
    public void testGetBoolean() throws Exception {
        assertTrue(new TrueBooleanProvider().getBoolean());
    }
}
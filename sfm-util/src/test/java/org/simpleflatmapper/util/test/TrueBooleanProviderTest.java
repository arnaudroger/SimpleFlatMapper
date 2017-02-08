package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.TrueBooleanProvider;

import static org.junit.Assert.*;

public class TrueBooleanProviderTest {

    @Test
    public void testGetBoolean() throws Exception {
        assertTrue(TrueBooleanProvider.INSTANCE.getBoolean());
    }
}
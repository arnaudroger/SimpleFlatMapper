package org.sfm.utils;

import org.junit.Test;

import static org.junit.Assert.fail;

public class AssertsTest {

    @Test
    public void testFailOnNull() {
        try {
            Asserts.requireNonNull("n", null);
            fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testSucceedOnNonNull() {
        Asserts.requireNonNull("n", "n");

    }
}

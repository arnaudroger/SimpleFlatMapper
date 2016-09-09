package org.simpleflatmapper.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ErrorHelperTest {


    @Test
    public void testErrorHelper() {
        IOException e = new IOException();
        try {
            rethrow(e);
        } catch (Exception ee) {
            assertSame(e, ee);
        }
    }

    private void rethrow(IOException e) {
        ErrorHelper.rethrow(e);
    }
}
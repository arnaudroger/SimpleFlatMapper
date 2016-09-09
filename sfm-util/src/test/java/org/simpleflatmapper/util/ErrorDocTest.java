package org.simpleflatmapper.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ErrorDocTest {

    @Test
    public void testErrorDoc() {
        assertNotNull(ErrorDoc.toUrl("aa"));
        assertNotNull(ErrorDoc.toUrl(null));
    }

}
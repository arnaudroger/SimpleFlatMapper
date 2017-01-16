package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ErrorDoc;

import static org.junit.Assert.*;

public class ErrorDocTest {

    @Test
    public void testErrorDoc() {
        assertNotNull(ErrorDoc.toUrl("aa"));
        assertNotNull(ErrorDoc.toUrl(null));
    }

}
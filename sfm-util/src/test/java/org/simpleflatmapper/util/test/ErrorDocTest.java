package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ErrorDoc;

import static org.junit.Assert.*;

public class ErrorDocTest {

    @Test
    public void testErrorDoc() {
        assertEquals("https://github.com/arnaudroger/SimpleFlatMapper/wiki/Errors_CSFM_GETTER_NOT_FOUND", ErrorDoc.CSFM_GETTER_NOT_FOUND.toUrl());
    }

}
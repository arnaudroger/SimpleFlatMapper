package org.sfm.csv.impl;


import org.junit.Test;

import static org.junit.Assert.assertNull;

public class CellValueReaderFactoryImplTest {


    @Test
    public void testDoesNotReaderAReaderForJavaSqlDate() {
        assertNull(new CellValueReaderFactoryImpl().getReader(java.sql.Date.class, 1));
    }


}

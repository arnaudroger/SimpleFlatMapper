package org.sfm.csv.impl.cellreader;


import org.junit.Test;

import static org.junit.Assert.assertNull;

public class DateCellValueReaderTest {

    @Test
    public void testReturnNullOnEmptyString() {
        assertNull(new DateCellValueReader(0).read(new char[10], 2, 0, null));
    }
}

package org.simpleflatmapper.csv.impl.cellreader;


import org.junit.Test;

import java.util.TimeZone;

import static org.junit.Assert.assertNull;

public class DateCellValueReaderTest {

    @Test
    public void testReturnNullOnEmptyString() {
        assertNull(new DateCellValueReader(0, "yyyyMMdd", TimeZone.getDefault()).read(new char[10], 2, 0, null));
    }
}

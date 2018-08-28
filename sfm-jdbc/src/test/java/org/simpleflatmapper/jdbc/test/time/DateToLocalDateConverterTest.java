package org.simpleflatmapper.jdbc.test.time;

import org.junit.Test;
import org.simpleflatmapper.jdbc.converter.time.DateToLocalDateConverter;

import java.sql.Date;

import static org.junit.Assert.*;

public class DateToLocalDateConverterTest {

    DateToLocalDateConverter converter = new DateToLocalDateConverter();
    @Test
    public void testNull() throws Exception {
        assertNull(converter.convert(null, null));
    }

    @Test
    public void testDate() throws Exception {
        Date date = new Date(System.currentTimeMillis());
        assertEquals(date.toLocalDate(), converter.convert(date, null ));
    }
}
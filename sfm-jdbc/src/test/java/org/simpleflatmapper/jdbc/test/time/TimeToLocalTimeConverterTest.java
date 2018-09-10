package org.simpleflatmapper.jdbc.test.time;

import org.junit.Test;
import org.simpleflatmapper.jdbc.converter.time.TimeToLocalTimeConverter;

import java.sql.Time;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TimeToLocalTimeConverterTest {

    TimeToLocalTimeConverter converter = new TimeToLocalTimeConverter();
    @Test
    public void testConvertTime() throws Exception {
        Time time = new Time(System.currentTimeMillis());
        LocalTime localTime = converter.convert(time, null);
        assertEquals(time.toLocalTime(), localTime);
    }

    @Test
    public void testConvertNull() throws Exception {
        assertNull(converter.convert(null, null));
    }
}
package org.sfm.map.column.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.sfm.csv.CsvColumnDefinition;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class JodaHelperTest {

    @Test
    public void testFormatterFailWhenEmpty() {
        try {
            JodaHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY);
            fail();
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testFormatterFromString() {
        final DateTimeFormatter yyyyMMdd = JodaHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY.addDateFormat("yyyyMMdd"));
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("yyyyMMdd").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.getDefault(), yyyyMMdd.getZone());
    }

    @Test
    public void testFormatterFromFormatter() {
        final DateTimeFormatter yyyyMMdd = JodaHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY.add(new JodaDateTimeFormatterProperty(DateTimeFormat.forPattern("MMddyyyy"))));
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("MMddyyyy").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.getDefault(), yyyyMMdd.getZone());

    }

    @Test
    public void testFormatterFromFormatterWithOwnTZ() {
        final DateTimeFormatter yyyyMMdd = JodaHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY.add(new JodaDateTimeFormatterProperty(DateTimeFormat.forPattern("ddMMyyyy").withZone(DateTimeZone.forID("America/Chicago")))));
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("ddMMyyyy").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.forID("America/Chicago"), yyyyMMdd.getZone());
    }


    @Test
    public void testFormatterFromFormatterWithSpecifiedTZ() {
        final DateTimeFormatter yyyyMMdd = JodaHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY.add(new JodaDateTimeFormatterProperty(DateTimeFormat.forPattern("ddMMyyyy").withZone(DateTimeZone.forID("America/Chicago")))).addTimeZone(TimeZone.getTimeZone("America/New_York")));
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("ddMMyyyy").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.forID("America/New_York"), yyyyMMdd.getZone());
    }
    @Test
    public void testGetDateTimeZoneWithNone() {
        assertEquals(null, JodaHelper.getDateTimeZone(CsvColumnDefinition.IDENTITY));
    }

    @Test
    public void testGetDateTimeZoneFromTimeZone() {
        assertEquals(DateTimeZone.forID("America/Chicago"), JodaHelper.getDateTimeZone(CsvColumnDefinition.IDENTITY.addTimeZone(TimeZone.getTimeZone("America/Chicago"))));
    }

    @Test
    public void testGetDateTimeZoneFromDateTimeZone() {
        assertEquals(DateTimeZone.forID("America/Chicago"), JodaHelper.getDateTimeZone(CsvColumnDefinition.IDENTITY.add(new JodaDateTimeZoneProperty(DateTimeZone.forID("America/Chicago")))));
    }

}
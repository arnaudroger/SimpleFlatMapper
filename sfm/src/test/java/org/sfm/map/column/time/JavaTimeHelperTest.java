package org.sfm.map.column.time;

import org.junit.Test;
import org.sfm.csv.CsvColumnDefinition;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static org.junit.Assert.*;


public class JavaTimeHelperTest {

    @Test
    public void testFormatterFailWhenEmpty() {
        try {
            JavaTimeHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY);
            fail();
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testFormatterFromString() {
        final DateTimeFormatter yyyyMMdd = JavaTimeHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY.addDateFormat("yyyyMMdd"));
        assertEquals(DateTimeFormatter.ofPattern("yyyyMMdd").toString(), yyyyMMdd.toString());
        assertEquals(ZoneId.systemDefault(), yyyyMMdd.getZone());
    }

    @Test
    public void testFormatterFromFormatter() {
        final DateTimeFormatter yyyyMMdd = JavaTimeHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY.add(new JavaDateTimeFormatterProperty(DateTimeFormatter.ofPattern("MMddyyyy"))));
        assertEquals(DateTimeFormatter.ofPattern("MMddyyyy").toString(), yyyyMMdd.toString());
        assertEquals(ZoneId.systemDefault(), yyyyMMdd.getZone());

    }

    @Test
    public void testFormatterFromFormatterWithOwnTZ() {
        final DateTimeFormatter yyyyMMdd = JavaTimeHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY.add(new JavaDateTimeFormatterProperty(DateTimeFormatter.ofPattern("ddMMyyyy").withZone(ZoneId.of("America/Chicago")))));
        assertEquals(DateTimeFormatter.ofPattern("ddMMyyyy").toString(), yyyyMMdd.toString());
        assertEquals(ZoneId.of("America/Chicago"), yyyyMMdd.getZone());
    }


    @Test
    public void testFormatterFromFormatterWithSpecifiedTZ() {
        final DateTimeFormatter yyyyMMdd = JavaTimeHelper.getDateTimeFormatter(CsvColumnDefinition.IDENTITY.add(new JavaDateTimeFormatterProperty(DateTimeFormatter.ofPattern("ddMMyyyy").withZone(ZoneId.of("America/Chicago")))).addTimeZone(TimeZone.getTimeZone("America/New_York")));
        assertEquals(DateTimeFormatter.ofPattern("ddMMyyyy").toString(), yyyyMMdd.toString());
        assertEquals(ZoneId.of("America/New_York"), yyyyMMdd.getZone());
    }

    @Test
    public void testGetZoneIdDefault() {
        assertEquals(ZoneId.systemDefault(), JavaTimeHelper.getZoneIdOrDefault(CsvColumnDefinition.IDENTITY));
    }

    @Test
         public void testGetZoneIdFromTimeZone() {
        assertEquals(ZoneId.of("America/Chicago"), JavaTimeHelper.getZoneIdOrDefault(CsvColumnDefinition.IDENTITY.addTimeZone(TimeZone.getTimeZone("America/Chicago"))));
    }

    @Test
    public void testGetZoneIdFromZoneId() {
        assertEquals(ZoneId.of("America/Chicago"), JavaTimeHelper.getZoneIdOrDefault(CsvColumnDefinition.IDENTITY.add(new JavaZoneIdProperty(ZoneId.of("America/Chicago")))));
    }
}
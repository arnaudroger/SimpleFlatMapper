package org.sfm.map.column.time;

import org.junit.Test;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.map.column.TimeZoneProperty;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static org.junit.Assert.*;


public class JavaTimeHelperTest {

    public static final ZoneId CHICAGO_TZ = ZoneId.of("America/Chicago");

    @Test
    public void testFormatterFailWhenEmpty() {
        try {
            JavaTimeHelper.getDateTimeFormatters(CsvColumnDefinition.IDENTITY);
            fail();
        } catch(IllegalStateException e) {
        }
    }

    @Test
    public void testFormatterFromString() {
        final DateTimeFormatter[] yyyyMMdd = JavaTimeHelper.getDateTimeFormatters(CsvColumnDefinition.IDENTITY.addDateFormat("yyyyMMdd"));
        assertEquals(DateTimeFormatter.ofPattern("yyyyMMdd").toString(), yyyyMMdd[0].toString());
        assertEquals(ZoneId.systemDefault(), yyyyMMdd[0].getZone());
    }

    @Test
    public void testFormatterFromFormatter() {
        final DateTimeFormatter[] yyyyMMdd = JavaTimeHelper.getDateTimeFormatters(CsvColumnDefinition.IDENTITY.add(new JavaDateTimeFormatterProperty(DateTimeFormatter.ofPattern("MMddyyyy"))));
        assertEquals(DateTimeFormatter.ofPattern("MMddyyyy").toString(), yyyyMMdd[0].toString());
        assertEquals(ZoneId.systemDefault(), yyyyMMdd[0].getZone());

    }

    @Test
    public void testFormatterFromFormatterWithOwnTZ() {
        final DateTimeFormatter[] yyyyMMdd = JavaTimeHelper.getDateTimeFormatters(CsvColumnDefinition.IDENTITY.add(new JavaDateTimeFormatterProperty(DateTimeFormatter.ofPattern("ddMMyyyy").withZone(ZoneId.of("America/Chicago")))));
        assertEquals(DateTimeFormatter.ofPattern("ddMMyyyy").toString(), yyyyMMdd[0].toString());
        assertEquals(ZoneId.of("America/Chicago"), yyyyMMdd[0].getZone());
    }


    @Test
    public void testFormatterFromFormatterWithSpecifiedTZ() {
        final DateTimeFormatter[] yyyyMMdd = JavaTimeHelper.getDateTimeFormatters(CsvColumnDefinition.IDENTITY.add(new JavaDateTimeFormatterProperty(DateTimeFormatter.ofPattern("ddMMyyyy").withZone(ZoneId.of("America/Chicago")))).addTimeZone(TimeZone.getTimeZone("America/New_York")));
        assertEquals(DateTimeFormatter.ofPattern("ddMMyyyy").toString(), yyyyMMdd[0].toString());
        assertEquals(ZoneId.of("America/New_York"), yyyyMMdd[0].getZone());
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
        assertEquals(CHICAGO_TZ, JavaTimeHelper.getZoneIdOrDefault(CsvColumnDefinition.IDENTITY.add(new JavaZoneIdProperty(ZoneId.of("America/Chicago")))));
    }

    @Test
    public void testGetDateTimeZoneFromParams() {
        assertEquals(CHICAGO_TZ, JavaTimeHelper.getZoneIdOrDefault(new Object[]{CsvColumnDefinition.IDENTITY.addTimeZone(TimeZone.getTimeZone("America/Chicago"))}));
        assertEquals(CHICAGO_TZ, JavaTimeHelper.getZoneIdOrDefault(new Object[]{CsvColumnDefinition.IDENTITY.add(new JavaZoneIdProperty(CHICAGO_TZ))}));
        assertEquals(CHICAGO_TZ, JavaTimeHelper.getZoneIdOrDefault(new Object[]{TimeZone.getTimeZone("America/Chicago")}));
        assertEquals(CHICAGO_TZ, JavaTimeHelper.getZoneIdOrDefault(new Object[]{CHICAGO_TZ}));
        assertEquals(CHICAGO_TZ, JavaTimeHelper.getZoneIdOrDefault(new Object[]{new TimeZoneProperty(TimeZone.getTimeZone("America/Chicago"))}));
        assertEquals(CHICAGO_TZ, JavaTimeHelper.getZoneIdOrDefault(new Object[]{new JavaZoneIdProperty(CHICAGO_TZ)}));
    }
}
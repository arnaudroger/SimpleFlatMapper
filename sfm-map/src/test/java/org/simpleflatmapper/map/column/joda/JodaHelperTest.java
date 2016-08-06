package org.simpleflatmapper.map.column.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.simpleflatmapper.map.column.DateFormatProperty;
import org.simpleflatmapper.map.column.TimeZoneProperty;
import org.simpleflatmapper.util.date.joda.JodaTimeHelper;

import java.util.TimeZone;

import static org.junit.Assert.*;

public class JodaHelperTest {

    private static final DateTimeZone CHICAGO_TZ = DateTimeZone.forID("America/Chicago");
    private static final DateTimeZone NY_TZ = DateTimeZone.forID("America/New_York");

    @SuppressWarnings("EmptyCatchBlock")
    @Test
    public void testFormatterFailWhenEmpty() {
        try {
            JodaTimeHelper.getDateTimeFormatters();
            fail();
        } catch(IllegalStateException e) {}
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testFormatterFromString() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(new DateFormatProperty("yyyyMMdd"))[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("yyyyMMdd").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.getDefault(), yyyyMMdd.getZone());
    }

    @Test
    public void testFormatterFromFormatter() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(new JodaDateTimeFormatterProperty(DateTimeFormat.forPattern("MMddyyyy")))[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("MMddyyyy").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.getDefault(), yyyyMMdd.getZone());

    }

    @Test
    public void testFormatterFromFormatterWithOwnTZ() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(new JodaDateTimeFormatterProperty(DateTimeFormat.forPattern("ddMMyyyy").withZone(CHICAGO_TZ)))[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("ddMMyyyy").withZone(CHICAGO_TZ).print(instant), yyyyMMdd.print(instant));
        assertEquals(CHICAGO_TZ, yyyyMMdd.getZone());
    }


    @Test
    public void testFormatterFromFormatterWithSpecifiedTZ() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(new JodaDateTimeFormatterProperty(DateTimeFormat.forPattern("ddMMyyyy").withZone(CHICAGO_TZ)), new TimeZoneProperty(TimeZone.getTimeZone("America/New_York")))[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("ddMMyyyy").withZone(NY_TZ).print(instant), yyyyMMdd.print(instant));
        assertEquals(NY_TZ, yyyyMMdd.getZone());
    }
    @Test
    public void testGetDateTimeZoneWithNone() {
        assertEquals(DateTimeZone.getDefault(), JodaTimeHelper.getDateTimeZoneOrDefault());
    }

    @Test
    public void testGetDateTimeZoneFromTimeZone() {
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(new TimeZoneProperty(TimeZone.getTimeZone("America/Chicago"))));
    }

    @Test
    public void testGetDateTimeZoneFromDateTimeZone() {
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(new JodaDateTimeZoneProperty(CHICAGO_TZ)));
    }


    @Test
    public void testGetDateTimeZoneFromParams() {
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(new Object[]{TimeZone.getTimeZone("America/Chicago")}));
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(new Object[]{CHICAGO_TZ}));
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(new Object[]{new TimeZoneProperty(TimeZone.getTimeZone("America/Chicago"))}));
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(new Object[]{new JodaDateTimeZoneProperty(CHICAGO_TZ)}));
    }

}
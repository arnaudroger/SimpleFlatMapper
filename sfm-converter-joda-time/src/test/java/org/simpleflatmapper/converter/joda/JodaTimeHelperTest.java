package org.simpleflatmapper.converter.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.simpleflatmapper.converter.joda.impl.JodaTimeHelper;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.date.DateFormatSupplier;

import java.util.TimeZone;

import static org.junit.Assert.*;

public class JodaTimeHelperTest {

    private static final DateTimeZone CHICAGO_TZ = DateTimeZone.forID("America/Chicago");
    public static final Supplier<DateTimeZone> DATE_TIME_ZONE_SUPPLIER = new Supplier<DateTimeZone>() {
        @Override
        public DateTimeZone get() {
            return CHICAGO_TZ;
        }
    };
    public static final Supplier<DateTimeFormatter> DATE_TIME_FORMATTER_SUPPLIER_TZ = new Supplier<DateTimeFormatter>() {
        @Override
        public DateTimeFormatter get() {
            return DateTimeFormat.forPattern("ddMMyyyy").withZone(CHICAGO_TZ);
        }
    };
    private static final DateTimeZone NY_TZ = DateTimeZone.forID("America/New_York");
    public static final DateFormatSupplier DATE_FORMAT_SUPPLIER = new DateFormatSupplier() {
        @Override
        public String get() {
            return "yyyyMMdd";
        }
    };
    public static final Supplier<DateTimeFormatter> DATE_TIME_FORMATTER_SUPPLIER = new Supplier<DateTimeFormatter>() {
        @Override
        public DateTimeFormatter get() {
            return DateTimeFormat.forPattern("MMddyyyy");
        }
    };
    public static final Supplier<TimeZone> TIME_ZONE_SUPPLIER = new Supplier<TimeZone>() {
        @Override
        public TimeZone get() {
            return TimeZone.getTimeZone("America/New_York");
        }
    };

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
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(DATE_FORMAT_SUPPLIER)[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("yyyyMMdd").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.getDefault(), yyyyMMdd.getZone());
    }

    @Test
    public void testFormatterFromFormatter() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(DATE_TIME_FORMATTER_SUPPLIER)[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("MMddyyyy").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.getDefault(), yyyyMMdd.getZone());

    }

    @Test
    public void testFormatterFromFormatterWithOwnTZ() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(DATE_TIME_FORMATTER_SUPPLIER_TZ)[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("ddMMyyyy").withZone(CHICAGO_TZ).print(instant), yyyyMMdd.print(instant));
        assertEquals(CHICAGO_TZ, yyyyMMdd.getZone());
    }


    @Test
    public void testFormatterFromFormatterWithSpecifiedTZ() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(DATE_TIME_FORMATTER_SUPPLIER_TZ, TIME_ZONE_SUPPLIER)[0];
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
        assertEquals(NY_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(TIME_ZONE_SUPPLIER));
    }

    @Test
    public void testGetDateTimeZoneFromDateTimeZone() {
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(DATE_TIME_ZONE_SUPPLIER));
    }


    @Test
    public void testGetDateTimeZoneFromParams() {
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(new Object[]{TimeZone.getTimeZone("America/Chicago")}));
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(new Object[]{CHICAGO_TZ}));
        assertEquals(NY_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(new Object[]{TIME_ZONE_SUPPLIER}));
        assertEquals(CHICAGO_TZ, JodaTimeHelper.getDateTimeZoneOrDefault(DATE_TIME_ZONE_SUPPLIER));
    }

}
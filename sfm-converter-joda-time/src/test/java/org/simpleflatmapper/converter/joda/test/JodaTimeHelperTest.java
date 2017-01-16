package org.simpleflatmapper.converter.joda.test;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.simpleflatmapper.converter.joda.JodaDateTimeFormatterProperty;
import org.simpleflatmapper.converter.joda.JodaDateTimeZoneProperty;
import org.simpleflatmapper.converter.joda.impl.JodaTimeHelper;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.date.DateFormatSupplier;
import org.simpleflatmapper.util.date.DefaultDateFormatSupplier;

import java.util.TimeZone;

import static org.junit.Assert.*;

public class JodaTimeHelperTest {

    private static final DateTimeZone CHICAGO_TZ = DateTimeZone.forID("America/Chicago");
    public static final Supplier<DateTimeZone> DATE_TIME_ZONE_SUPPLIER = new JodaDateTimeZoneProperty(CHICAGO_TZ);
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
    public static final Supplier<DateTimeFormatter> DATE_TIME_FORMATTER_SUPPLIER = new JodaDateTimeFormatterProperty(DateTimeFormat.forPattern("MMddyyyy"));
    public static final Supplier<TimeZone> TIME_ZONE_SUPPLIER = new Supplier<TimeZone>() {
        @Override
        public TimeZone get() {
            return TimeZone.getTimeZone("America/New_York");
        }
    };

    @SuppressWarnings("EmptyCatchBlock")
    @Test
    public void testFormattersFailWhenEmpty() {
        try {
            JodaTimeHelper.getDateTimeFormatters();
            fail();
        } catch(IllegalStateException e) {}
    }

    @Test
    public void testGetFormatterReturnNullWhenEmpty() {
        assertNull(JodaTimeHelper.getDateTimeFormatter());
    }

    @Test
    public void testGetFormatterAndDefaultFormat() {
        LocalDateTime localDateTime = LocalDateTime.now();

        assertEquals(DateTimeFormat.forPattern("yyyy").print(localDateTime),JodaTimeHelper.getDateTimeFormatter(new DefaultDateFormatSupplier() {
            @Override
            public String get() {
                return "yyyy";
            }
        }).print(localDateTime));

        assertEquals(DateTimeFormat.fullDate().print(localDateTime) ,JodaTimeHelper.getDateTimeFormatter(new DefaultDateFormatSupplier() {
            @Override
            public String get() {
                return "yyyy";
            }
        }, DateTimeFormat.fullDate()).print(localDateTime));
    }

    @Test
    public void testGetFormattersAndDefaultFormat() {
        LocalDateTime localDateTime = LocalDateTime.now();
        assertEquals(DateTimeFormat.forPattern("yyyy").print(localDateTime),JodaTimeHelper.getDateTimeFormatters(new DefaultDateFormatSupplier() {
            @Override
            public String get() {
                return "yyyy";
            }
        })[0].print(localDateTime));

        DateTimeFormatter[] dateTimeFormatters = JodaTimeHelper.getDateTimeFormatters(new DefaultDateFormatSupplier() {
            @Override
            public String get() {
                return "yyyy";
            }
        }, DateTimeFormat.fullDate());
        assertEquals(1, dateTimeFormatters.length);
        assertEquals(DateTimeFormat.fullDate().print(localDateTime), dateTimeFormatters[0].print(localDateTime));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testFormattersFromString() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(DATE_FORMAT_SUPPLIER)[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("yyyyMMdd").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.getDefault(), yyyyMMdd.getZone());
    }

    @Test
    public void testFormattersFromFormatter() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(DATE_TIME_FORMATTER_SUPPLIER)[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("MMddyyyy").print(instant), yyyyMMdd.print(instant));
        assertEquals(DateTimeZone.getDefault(), yyyyMMdd.getZone());

    }

    @Test
    public void testFormattersFromFormatterWithOwnTZ() {
        final DateTimeFormatter yyyyMMdd = JodaTimeHelper.getDateTimeFormatters(DATE_TIME_FORMATTER_SUPPLIER_TZ)[0];
        final long instant = System.currentTimeMillis();
        assertEquals(DateTimeFormat.forPattern("ddMMyyyy").withZone(CHICAGO_TZ).print(instant), yyyyMMdd.print(instant));
        assertEquals(CHICAGO_TZ, yyyyMMdd.getZone());
    }


    @Test
    public void testFormattersFromFormatterWithSpecifiedTZ() {
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
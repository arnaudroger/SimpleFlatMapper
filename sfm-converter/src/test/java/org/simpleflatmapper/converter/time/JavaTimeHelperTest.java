package org.simpleflatmapper.converter.time;

import org.junit.Test;
import org.simpleflatmapper.converter.impl.time.JavaTimeHelper;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.date.DateFormatSupplier;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static org.junit.Assert.*;


public class JavaTimeHelperTest {

    public static final ZoneId ZONE_ID_CHICAGO = ZoneId.of("America/Chicago");
    public static final ZoneId ZONE_ID_NY = ZoneId.of("America/New_York");
    public static final TimeZone TIME_ZONE_NY = TimeZone.getTimeZone("America/New_York");

    public static final ZoneId ZONE_ID = ZoneId.of("America/Chicago");
    public static final Supplier<DateTimeFormatter> DATE_TIME_FORMATTER_SUPPLIER = new Supplier<DateTimeFormatter>() {
        @Override
        public DateTimeFormatter get() {
            return DateTimeFormatter.ofPattern("MMddyyyy");
        }
    };
    public static final Supplier<DateTimeFormatter> DATE_TIME_FORMATTER_WITH_TZ_SUPPLIER = new Supplier<DateTimeFormatter>() {
        @Override
        public DateTimeFormatter get() {
            return DateTimeFormatter.ofPattern("ddMMyyyy").withZone(ZONE_ID);
        }
    };
    public static final Supplier<TimeZone> TIME_ZONE_SUPPLIER = new Supplier<TimeZone>() {

        @Override
        public TimeZone get() {
            return TIME_ZONE_NY;
        }
    };
    public static final Supplier<ZoneId> ZONE_ID_SUPPLIER = new Supplier<ZoneId>() {
        @Override
        public ZoneId get() {
            return ZoneId.of("America/Chicago");
        }
    };

    @Test
    public void testFormatterFailWhenEmpty() {
        try {
            JavaTimeHelper.getDateTimeFormatters();
            fail();
        } catch(IllegalStateException e) {
        }
    }

    @Test
    public void testFormatterFromString() {
        final DateTimeFormatter[] yyyyMMdd = JavaTimeHelper.getDateTimeFormatters(new DateFormatSupplier() {
            @Override
            public String get() {
                return "yyyyMMdd";
            }
        });
        assertEquals(DateTimeFormatter.ofPattern("yyyyMMdd").toString(), yyyyMMdd[0].toString());
        assertEquals(ZoneId.systemDefault(), yyyyMMdd[0].getZone());
    }

    @Test
    public void testFormatterFromFormatter() {
        final DateTimeFormatter[] yyyyMMdd = JavaTimeHelper.getDateTimeFormatters(DATE_TIME_FORMATTER_SUPPLIER);
        assertEquals(DateTimeFormatter.ofPattern("MMddyyyy").toString(), yyyyMMdd[0].toString());
        assertEquals(ZoneId.systemDefault(), yyyyMMdd[0].getZone());

    }

    @Test
    public void testFormatterFromFormatterWithOwnTZ() {
        final DateTimeFormatter[] yyyyMMdd = JavaTimeHelper.getDateTimeFormatters(DATE_TIME_FORMATTER_WITH_TZ_SUPPLIER);
        assertEquals(DateTimeFormatter.ofPattern("ddMMyyyy").toString(), yyyyMMdd[0].toString());
        assertEquals(ZoneId.of("America/Chicago"), yyyyMMdd[0].getZone());
    }


    @Test
    public void testFormatterFromFormatterWithSpecifiedTZ() {
        final DateTimeFormatter[] yyyyMMdd = JavaTimeHelper.getDateTimeFormatters(DATE_TIME_FORMATTER_WITH_TZ_SUPPLIER, TIME_ZONE_SUPPLIER);
        assertEquals(DateTimeFormatter.ofPattern("ddMMyyyy").toString(), yyyyMMdd[0].toString());
        assertEquals(ZoneId.of("America/New_York"), yyyyMMdd[0].getZone());
    }

    @Test
    public void testGetZoneIdDefault() {
        assertEquals(ZoneId.systemDefault(), JavaTimeHelper.getZoneIdOrDefault());
    }

    @Test
     public void testGetZoneIdFromTimeZone() {
        assertEquals(ZONE_ID_NY, JavaTimeHelper.getZoneIdOrDefault(TIME_ZONE_SUPPLIER));
    }

    @Test
    public void testGetZoneIdFromZoneId() {
        assertEquals(ZONE_ID_CHICAGO, JavaTimeHelper.getZoneIdOrDefault(ZONE_ID_SUPPLIER));
    }

    @Test
    public void testGetDateTimeZoneFromParams() {
        assertEquals(ZONE_ID_NY, JavaTimeHelper.getZoneIdOrDefault(TIME_ZONE_SUPPLIER));
        assertEquals(ZONE_ID_CHICAGO, JavaTimeHelper.getZoneIdOrDefault(ZONE_ID_SUPPLIER));
        assertEquals(ZONE_ID_CHICAGO, JavaTimeHelper.getZoneIdOrDefault(TimeZone.getTimeZone("America/Chicago")));
        assertEquals(ZONE_ID_CHICAGO, JavaTimeHelper.getZoneIdOrDefault(ZONE_ID_CHICAGO));
    }
}
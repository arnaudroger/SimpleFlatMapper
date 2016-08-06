package org.simpleflatmapper.reflect.getter.time;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class JavaYearMonthFromObjectGetterTest {

    @Test
    public void testGet() throws Exception {

        ZoneId zoneId = ZoneId.systemDefault();
        YearMonth yearMonth = YearMonth.now();
        Date now = new Date();

        Getter<Object, YearMonth> getter =
                GetterFactoryHelper.getGetter(YearMonth.class,
                        null,
                        yearMonth,
                        yearMonth.atEndOfMonth().atTime(1, 0).atZone(zoneId),
                        now,
                        yearMonth.getYear() * 100 + yearMonth.getMonthValue(),
                        "333333");

        assertNull(getter.get(null));
        assertEquals(yearMonth, getter.get(null));
        assertEquals(yearMonth, getter.get(null));
        assertEquals(yearMonth, getter.get(null));
        assertEquals(yearMonth, getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();
    }
}
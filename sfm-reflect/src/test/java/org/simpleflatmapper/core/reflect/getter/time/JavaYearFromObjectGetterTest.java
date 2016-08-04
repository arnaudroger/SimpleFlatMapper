package org.simpleflatmapper.core.reflect.getter.time;

import org.junit.Test;
import org.simpleflatmapper.core.reflect.Getter;

import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JavaYearFromObjectGetterTest {

    @Test
    public void testGet() throws Exception {

        ZoneId zoneId = ZoneId.systemDefault();
        Year year = Year.now();
        Date now = new Date();

        Getter<Object, Year> getter =
                GetterFactoryHelper.getGetter(Year.class, null,
                        year,
                        year.atMonth(6).atEndOfMonth().atTime(1, 0).atZone(zoneId),
                        now,
                        year.getValue(),
                        "333333");

        assertNull(getter.get(null));
        assertEquals(year, getter.get(null));
        assertEquals(year, getter.get(null));
        assertEquals(year, getter.get(null));
        assertEquals(year, getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();
    }
}
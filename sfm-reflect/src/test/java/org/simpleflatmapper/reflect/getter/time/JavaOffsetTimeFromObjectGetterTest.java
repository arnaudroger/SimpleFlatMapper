package org.simpleflatmapper.reflect.getter.time;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class JavaOffsetTimeFromObjectGetterTest {


    @Test
    public void testGet() throws Exception {

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetTime offsetTime = OffsetTime.now(zoneId);

        Getter<Object, OffsetTime> getter =
                GetterFactoryHelper.getGetter(OffsetTime.class,
                        null,
                        offsetTime,
                        offsetTime.atDate(LocalDate.now()),
                        offsetTime.atDate(LocalDate.now()).atZoneSameInstant(zoneId),
                        offsetTime.atDate(LocalDate.now()).toLocalDateTime(),
                        offsetTime.atDate(LocalDate.now()).toLocalTime(),
                        offsetTime.atDate(LocalDate.now()).toInstant(),
                        Date.from(offsetTime.atDate(LocalDate.now()).toInstant()),
                        "333333");

        assertNull(getter.get(null));
        assertEquals(offsetTime, getter.get(null));
        assertEquals(offsetTime, getter.get(null));
        assertEquals(offsetTime, getter.get(null));
        assertEquals(offsetTime, getter.get(null));
        assertEquals(offsetTime, getter.get(null));
        assertEquals(offsetTime, getter.get(null));
        assertEquals(offsetTime.truncatedTo(ChronoUnit.MILLIS), getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();
    }
}
package org.simpleflatmapper.reflect.getter.time;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class JavaLocalTimeFromObjectGetterTest {


    @Test
    public void testGet() throws Exception {

        ZoneId zoneId = ZoneId.systemDefault();
        LocalTime localTime = LocalTime.now();

        ZoneOffset offset = zoneId.getRules().getOffset(localTime.atDate(LocalDate.now()));
        Getter<Object, LocalTime> getter =
                GetterFactoryHelper.getGetter(LocalTime.class,
                        null,
                        localTime,
                        localTime.atDate(LocalDate.now()),
                        localTime.atDate(LocalDate.now()).atZone(zoneId),
                        localTime.atDate(LocalDate.now()).atOffset(offset),
                        localTime.atDate(LocalDate.now()).toLocalTime(),
                        localTime.atDate(LocalDate.now()).toInstant(offset),
                        Date.from(localTime.atDate(LocalDate.now()).toInstant(offset)),
                        "333333");

        assertNull(getter.get(null));
        assertEquals(localTime, getter.get(null));
        assertEquals(localTime, getter.get(null));
        assertEquals(localTime, getter.get(null));
        assertEquals(localTime, getter.get(null));
        assertEquals(localTime, getter.get(null));
        assertEquals(localTime, getter.get(null));
        assertEquals(localTime.truncatedTo(ChronoUnit.MILLIS), getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();
    }
}
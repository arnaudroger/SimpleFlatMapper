package org.simpleflatmapper.reflect.getter.time;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;

public class JavaZonedDateTimeFromObjectGetterTest {

    @Test
    public void testGet() throws Exception {

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        Getter<Object, ZonedDateTime> getter = GetterFactoryHelper.getGetter(ZonedDateTime.class,
                null,
                zonedDateTime,
                zonedDateTime.toInstant(),
                zonedDateTime.toLocalDateTime(),
                zonedDateTime.toOffsetDateTime(),
                Date.from(zonedDateTime.toInstant()),
                "333333");

        assertNull(getter.get(null));
        assertEquals(zonedDateTime, getter.get(null));
        assertEquals(zonedDateTime, getter.get(null));
        assertEquals(zonedDateTime, getter.get(null));
        assertEquals(zonedDateTime, getter.get(null));
        assertEquals(zonedDateTime.truncatedTo(ChronoUnit.MILLIS), getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();
    }
}
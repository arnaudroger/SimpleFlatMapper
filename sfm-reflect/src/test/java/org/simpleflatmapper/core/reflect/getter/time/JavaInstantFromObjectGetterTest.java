package org.simpleflatmapper.core.reflect.getter.time;

import org.junit.Test;
import org.simpleflatmapper.core.reflect.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class JavaInstantFromObjectGetterTest {


    @Test
    public void testGet() throws Exception {

        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = Instant.now();

        Getter<Object, Instant> getter =
                GetterFactoryHelper.getGetter(Instant.class,
                        null,
                        instant,
                        instant.atZone(zoneId),
                        instant.atZone(zoneId).toInstant(),
                        instant.atZone(zoneId).toLocalDateTime(),
                        instant.atZone(zoneId).toOffsetDateTime(),
                        Date.from(instant.atZone(zoneId).toInstant()),
                        instant.toEpochMilli(),
                        "333333");

        assertNull(getter.get(null));
        assertEquals(instant, getter.get(null));
        assertEquals(instant, getter.get(null));
        assertEquals(instant, getter.get(null));
        assertEquals(instant, getter.get(null));
        assertEquals(instant, getter.get(null));
        assertEquals(instant.truncatedTo(ChronoUnit.MILLIS), getter.get(null));
        assertEquals(instant.truncatedTo(ChronoUnit.MILLIS), getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();
    }
}
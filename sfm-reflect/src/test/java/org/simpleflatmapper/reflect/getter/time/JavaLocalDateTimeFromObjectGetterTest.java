package org.simpleflatmapper.reflect.getter.time;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class JavaLocalDateTimeFromObjectGetterTest {


    @Test
    public void testGet() throws Exception {

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.now(zoneId);

        Getter<Object, LocalDateTime> getter =
                GetterFactoryHelper.getGetter(LocalDateTime.class,
                        null,
                        localDateTime,
                        localDateTime.atZone(zoneId),
                        localDateTime.atZone(zoneId).toInstant(),
                        localDateTime.atZone(zoneId).toOffsetDateTime(),
                        Date.from(localDateTime.atZone(zoneId).toInstant()),
                        "333333");

        assertNull(getter.get(null));
        assertEquals(localDateTime, getter.get(null));
        assertEquals(localDateTime, getter.get(null));
        assertEquals(localDateTime, getter.get(null));
        assertEquals(localDateTime, getter.get(null));
        assertEquals(localDateTime.truncatedTo(ChronoUnit.MILLIS), getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();
    }
}
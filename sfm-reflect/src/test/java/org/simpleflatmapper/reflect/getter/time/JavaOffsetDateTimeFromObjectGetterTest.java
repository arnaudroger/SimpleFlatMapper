package org.simpleflatmapper.reflect.getter.time;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class JavaOffsetDateTimeFromObjectGetterTest {


    @Test
    public void testGet() throws Exception {

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);

        Getter<Object, OffsetDateTime> getter =
                GetterFactoryHelper.getGetter(OffsetDateTime.class,
                        null,
                        offsetDateTime,
                        offsetDateTime.toLocalDateTime(),
                        offsetDateTime.toInstant(),
                        offsetDateTime.atZoneSameInstant(zoneId),
                        Date.from(offsetDateTime.toInstant()),
                        "333333");

        assertNull(getter.get(null));
        assertEquals(offsetDateTime, getter.get(null));
        assertEquals(offsetDateTime, getter.get(null));
        assertEquals(offsetDateTime, getter.get(null));
        assertEquals(offsetDateTime, getter.get(null));
        assertEquals(offsetDateTime.truncatedTo(ChronoUnit.MILLIS), getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();
    }
}
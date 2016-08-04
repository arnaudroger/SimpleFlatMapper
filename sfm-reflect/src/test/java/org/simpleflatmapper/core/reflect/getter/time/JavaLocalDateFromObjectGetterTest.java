package org.simpleflatmapper.core.reflect.getter.time;

import org.junit.Test;
import org.simpleflatmapper.core.reflect.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class JavaLocalDateFromObjectGetterTest {

    @Test
    public void testGet() throws Exception {

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = LocalDate.now(zoneId);

        Getter<Object, LocalDate> getter =
                GetterFactoryHelper.getGetter(LocalDate.class,
                        null,
                        localDate,
                        localDate.atStartOfDay().atZone(zoneId),
                        localDate.atStartOfDay().atZone(zoneId).toInstant(),
                        localDate.atStartOfDay().atZone(zoneId).toOffsetDateTime(),
                        localDate.atStartOfDay(),
                        Date.from(localDate.atStartOfDay().atZone(zoneId).toInstant()),
                        "333333");

        assertNull(getter.get(null));
        assertEquals(localDate, getter.get(null));
        assertEquals(localDate, getter.get(null));
        assertEquals(localDate, getter.get(null));
        assertEquals(localDate, getter.get(null));
        assertEquals(localDate, getter.get(null));
        assertEquals(localDate, getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();
    }
}
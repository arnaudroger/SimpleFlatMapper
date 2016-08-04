package org.simpleflatmapper.core.reflect.getter.joda;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.simpleflatmapper.core.reflect.Getter;

import static org.junit.Assert.*;

public class JodaDateTimeFromDateGetterTest {

    @Test
    public void testGet() throws Exception {

        DateTimeZone zoneId = DateTimeZone.getDefault();
        DateTime dateTime = DateTime.now(zoneId);

        Getter<Object, DateTime> getter = GetterFactoryHelper.getGetter(DateTime.class,
                null,
                dateTime.toDate());

        assertNull(getter.get(null));
        assertEquals(dateTime, getter.get(null));

        getter.toString();
    }
}
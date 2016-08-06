package org.simpleflatmapper.reflect.getter.joda;

import org.joda.time.LocalTime;
import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;


import static org.junit.Assert.*;

public class JodaLocalTimeFromObjectGetterTest {
    @Test
    public void testGet() throws Exception {

        LocalTime localTime = LocalTime.now();

        Getter<Object, LocalTime> getter = GetterFactoryHelper.getGetter(LocalTime.class,
                null,
                localTime.toDateTimeToday().toDate());

        assertNull(getter.get(null));
        assertEquals(localTime, getter.get(null));

        getter.toString();
    }
}
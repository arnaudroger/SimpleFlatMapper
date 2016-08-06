package org.simpleflatmapper.reflect.getter.joda;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;


import static org.junit.Assert.*;

public class JodaLocalDateTimeFromDateGetterTest {
    @Test
    public void testGet() throws Exception {

        LocalDateTime localDate = LocalDateTime.now();

        Getter<Object, LocalDateTime> getter = GetterFactoryHelper.getGetter(LocalDateTime.class,
                null,
                localDate.toDate());

        assertNull(getter.get(null));
        assertEquals(localDate, getter.get(null));

        getter.toString();
    }
}
package org.simpleflatmapper.reflect.getter.joda;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;

import static org.junit.Assert.*;

public class DateToJodaLocalDateConverterTest {

    @Test
    public void testGet() throws Exception {

        LocalDate localDate = LocalDate.now();

        Getter<Object, LocalDate> getter = GetterFactoryHelper.getGetter(LocalDate.class,
                null,
                localDate.toDate());

        assertNull(getter.get(null));
        assertEquals(localDate, getter.get(null));

        getter.toString();
    }
}
package org.simpleflatmapper.converter.joda;

import org.junit.Test;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.impl.JavaBaseConverterFactoryProducer;

import java.util.Calendar;
import java.util.Date;

//IFJAVA8_START
//IFJAVA8_END
import static org.junit.Assert.*;

public class ConverterFactoryTest {

    public enum ENUM {
        type1, type2
    }
    @SuppressWarnings("unchecked")
    private <I, O> void testConverter(I i, O o) throws Exception {
        testConverter(i, o, (Class<? super I>)i.getClass(), (Class<? super O>)o.getClass());
    }

    private <I, O> void testConverter(I i, O o, Class<? super I> classi, Class<? super O> classo) throws Exception {
        final Converter<? super I, ? super O> converter = JavaBaseConverterFactoryProducer.getConverter(classi, classo);
        assertNotNull("Converter not null", converter);
        assertEquals(o, converter.convert(i));
        assertNotNull(converter.toString());
    }

    @Test
    public void testJodaTime() throws Exception {
        long time = System.currentTimeMillis();
        testConverter(new org.joda.time.LocalDateTime(time), new Date(time));
        testConverter(new org.joda.time.LocalTime(time), new Date(time));
        testConverter(new org.joda.time.LocalDate(time), trunc(new Date(time)));
        testConverter(new org.joda.time.Instant(time), new Date(time));
        testConverter(new org.joda.time.DateTime(time), new Date(time));
    }


    private Date trunc(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        return cal.getTime();
    }

}
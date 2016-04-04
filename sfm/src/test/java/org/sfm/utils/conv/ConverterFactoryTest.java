package org.sfm.utils.conv;

import org.junit.Test;
import org.sfm.beans.DbObject;

import javax.swing.text.Document;
import javax.xml.parsers.SAXParser;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

//IFJAVA8_START
import java.time.*;
//IFJAVA8_END
import static org.junit.Assert.*;
import static org.sfm.utils.conv.ConverterFactory.*;

public class ConverterFactoryTest {

    @SuppressWarnings("unchecked")
    private <I, O> void testConverter(I i, O o) throws Exception {
        testConverter(i, o, (Class<? super I>)i.getClass(), (Class<? super O>)o.getClass());
    }

    private <I, O> void testConverter(I i, O o, Class<? super I> classi, Class<? super O> classo) throws Exception {
        final Converter<? super I, ? super O> converter = getConverter(classi, classo);
        assertNotNull("Converter not null", converter);
        assertEquals(o, converter.convert(i));
        assertNotNull(converter.toString());
    }

    @Test
    public void testToStringConverter() throws Exception {
        testConverter("Hoy", "Hoy", Object.class, String.class);
    }

    @Test
    public void testNumberToNumberConverter() throws Exception {
        testConverter(13, (byte)13);
        testConverter((byte)13, 13);

        testConverter(13, (short)13);
        testConverter((short)13, 13);

        testConverter(13, 13);
        testConverter(13, 13);

        testConverter(13, (long)13);
        testConverter((long)13, 13);

        testConverter(13, (float)13);
        testConverter((float)13, 13);

        testConverter(13, (double)13);
        testConverter((double)13, 13);


        testConverter(13, new BigDecimal(13));
        testConverter(new BigDecimal(13), 13);

        testConverter(13, new BigInteger("13"));
        testConverter(new BigInteger("13"), 13);
    }

    @Test
    public void testIdentity() throws Exception {
        Object o = new Object();
        testConverter(o, o);
    }

    @Test
    public void testURLConverter() throws Exception {
        testConverter("http://url.net", new URL("http://url.net"));

        try {
            getConverter(String.class, URL.class).convert("blop");
            fail();
        } catch(ConversionException e) {
            // expected
        }
    }

    @Test
    public void testCharSequenceConverter() throws Exception {
        testConverter(new StringBuilder("hello"), "hello");
        testConverter("123",     Byte.valueOf((byte)123));
        testConverter("123",     Character.valueOf((char)123));
        testConverter("1234",    Short.valueOf((short)1234));
        testConverter("1234",    Integer.valueOf(1234));
        testConverter("1234",    Long.valueOf(1234));
        testConverter("1234.56", Float.valueOf(1234.56f));
        testConverter("1234.56", Double.valueOf(1234.56));
        testConverter("type2",   DbObject.Type.type2);

        final UUID uuid = UUID.randomUUID();
        testConverter(uuid.toString(), uuid);
    }

    @Test
    public void testNoConverter()  {
        assertNull(getConverter(Document.class, SAXParser.class));
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

    //IFJAVA8_START
    @Test
    public void testJavaTime() throws Exception {
        long time = System.currentTimeMillis();
        final Date date = new Date(time);
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        final ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        testConverter(localDateTime, date);
        testConverter(localDateTime.toLocalTime(), date);
        testConverter(localDateTime.toLocalDate(), trunc(date));
        testConverter(date.toInstant(), date);
        testConverter(zonedDateTime, date);
        testConverter(zonedDateTime.toOffsetDateTime(), date);
        testConverter(zonedDateTime.toOffsetDateTime().toOffsetTime(), date);
        testConverter(YearMonth.of(2016, Month.FEBRUARY),
                new SimpleDateFormat("yyyyMMdd").parse("20160201"));
        testConverter(Year.of(2016),
                new SimpleDateFormat("yyyyMMdd").parse("20160101"));

    }

    //IFJAVA8_END

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
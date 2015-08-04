package org.sfm.utils.conv;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.csv.impl.writer.ObjectAppendableSetter;

import javax.swing.text.Document;
import javax.xml.parsers.SAXParser;
import java.math.BigDecimal;
import java.net.URL;

import static org.junit.Assert.*;
import static org.sfm.utils.conv.ConverterFactory.*;

public class ConverterFactoryTest {

    @Test
    public void testToStringConverter() throws Exception {
        assertEquals("Hoy",
                getConverter(Object.class, String.class).convert("Hoy"));
    }

    @Test
    public void testNumberToNumberConverter() throws Exception {
        assertEquals(new Byte((byte) 13),
                getConverter(Integer.class, Byte.class).convert(13));
        assertEquals(new Short((short) 13),
                getConverter(Integer.class, Short.class).convert(13));
        assertEquals(new Integer(13),
                getConverter(Integer.class, Integer.class).convert(13));
        assertEquals(new Long(13),
                getConverter(Integer.class, Long.class).convert(13));
        assertEquals(new Float(13),
                getConverter(Integer.class, Float.class).convert(13));
        assertEquals(new Double(13),
                getConverter(Integer.class, Double.class).convert(13));
        assertEquals(new BigDecimal(13),
                getConverter(Integer.class, BigDecimal.class).convert(13));
    }

    @Test
    public void testIdentity() throws Exception {
        Object o = new Object();
        assertSame(o, getConverter(Object.class, Object.class).convert(o));
    }

    @Test
    public void testURLConverter() throws Exception {
        assertEquals(new URL("http://url.net"),
                getConverter(String.class, URL.class).convert("http://url.net"));

        try {
            getConverter(String.class, URL.class).convert("blop");
            fail();
        } catch(ConversionException e) {
            // expected
        }
    }

    @Test
    public void testCharSequenceConverter() throws Exception {
        assertEquals("hello", getConverter(CharSequence.class, String.class).convert(new StringBuilder("hello")));
        assertEquals(Byte.valueOf((byte)123), getConverter(CharSequence.class, Byte.class).convert("123"));
        assertEquals(Character.valueOf((char)123), getConverter(CharSequence.class, Character.class).convert("123"));
        assertEquals(Short.valueOf((short)1234), getConverter(CharSequence.class, Short.class).convert("1234"));
        assertEquals(Integer.valueOf(1234), getConverter(CharSequence.class, Integer.class).convert("1234"));
        assertEquals(Long.valueOf(1234), getConverter(CharSequence.class, Long.class).convert("1234"));
        assertEquals(Float.valueOf(1234.56f), getConverter(CharSequence.class, Float.class).convert("1234.56"), 0.00001);
        assertEquals(Double.valueOf(1234.56), getConverter(CharSequence.class, Double.class).convert("1234.56"), 0.00001);
        assertEquals(DbObject.Type.type2, getConverter(CharSequence.class, DbObject.Type.class).convert("type2"));
    }

    @Test
    public void testNoConverter()  {
        assertNull(getConverter(Document.class, SAXParser.class));
    }


    @Test
    public void testArrayConverter() {

    }

}
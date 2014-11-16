package org.sfm.utils.conv;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ConverterFactoryTest {

    @Test
    public void testToStringConverter() throws Exception {
        assertEquals("Hoy",
                ConverterFactory.getConverter(Object.class, String.class).convert("Hoy"));
    }

    @Test
    public void testNumberToNumberConverter() throws Exception {
        assertEquals(new Byte((byte) 13),
                ConverterFactory.getConverter(Integer.class, Byte.class).convert(13));
        assertEquals(new Short((short) 13),
                ConverterFactory.getConverter(Integer.class, Short.class).convert(13));
        assertEquals(new Integer(13),
                ConverterFactory.getConverter(Integer.class, Integer.class).convert(13));
        assertEquals(new Long(13),
                ConverterFactory.getConverter(Integer.class, Long.class).convert(13));
        assertEquals(new Float(13),
                ConverterFactory.getConverter(Integer.class, Float.class).convert(13));
        assertEquals(new Double(13),
                ConverterFactory.getConverter(Integer.class, Double.class).convert(13));
        assertEquals(new BigDecimal(13),
                ConverterFactory.getConverter(Integer.class, BigDecimal.class).convert(13));

    }
}
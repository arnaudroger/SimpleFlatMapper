package org.simpleflatmapper.converter.test;

import org.junit.Test;
import org.simpleflatmapper.converter.ComposedConverter;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ToStringConverter;
import org.simpleflatmapper.converter.impl.CharSequenceIntegerConverter;

import static org.junit.Assert.*;

public class ComposedConverterTest {

    @Test
    public void testComposedConverter() throws Exception {
        Converter<Object, String> converterToString = ToStringConverter.INSTANCE;
        Converter<CharSequence, Integer> converterToInteger = new CharSequenceIntegerConverter();

        ComposedConverter<Object, String, Integer> composedConverter =
                new ComposedConverter<Object, String, Integer>(converterToString, converterToInteger);

        assertEquals(1234, composedConverter.convert(new Object() {
            public String toString() {
                return "1234";
            }
        }, null).intValue());

    }
}
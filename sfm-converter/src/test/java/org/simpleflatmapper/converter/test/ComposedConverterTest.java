package org.simpleflatmapper.converter.test;

import org.junit.Test;
import org.simpleflatmapper.converter.ComposedContextualConverter;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ToStringConverter;
import org.simpleflatmapper.converter.impl.CharSequenceIntegerConverter;

import static org.junit.Assert.*;

public class ComposedConverterTest {

    @Test
    public void testComposedConverter() throws Exception {
        ContextualConverter<Object, String> converterToString = ToStringConverter.INSTANCE;
        ContextualConverter<CharSequence, Integer> converterToInteger = new CharSequenceIntegerConverter();

        ComposedContextualConverter<Object, String, Integer> composedConverter =
                new ComposedContextualConverter<Object, String, Integer>(converterToString, converterToInteger);

        assertEquals(1234, composedConverter.convert(new Object() {
            public String toString() {
                return "1234";
            }
        }, null).intValue());

    }
}
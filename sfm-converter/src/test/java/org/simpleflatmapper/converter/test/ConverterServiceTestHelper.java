package org.simpleflatmapper.converter.test;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterService;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ConverterServiceTestHelper {

    @SuppressWarnings("unchecked")
    public static <I, O> void testConverter(I i, O o) throws Exception {
        testConverter(i, o, (Class<? super I>)i.getClass(), (Class<? super O>)o.getClass());
    }

    public static <I, O> void testConverter(I i, O o, Class<I> classi, Class<O> classo, Object... params) throws Exception {
        final Converter<? super I, ? extends O> converter = ConverterService.getInstance().findConverter(classi, classo, params);
        assertNotNull("Converter not null", converter);
        assertEquals(o, converter.convert(i));
        assertNotNull(converter.toString());
    }

}
package org.simpleflatmapper.converter.test;

import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.converter.DefaultContextFactoryBuilder;


import java.util.Arrays;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ConverterServiceTestHelper {

    @SuppressWarnings("unchecked")
    public static <I, O> void testConverter(I i, O o, Object... params) throws Exception {
        testConverter(i, o, (Class<? super I>)i.getClass(), (Class<? super O>)o.getClass(), params);
    }

    public static <I, O> void testConverter(I i, O o, Class<I> classi, Class<O> classo, Object... params) throws Exception {

        if (hasZoneId(params)) {
            params = Arrays.copyOf(params, params.length + 1);
            params[params.length - 1] = TimeZone.getTimeZone("UTC");
        }
        DefaultContextFactoryBuilder defaultContextFactoryBuilder = new DefaultContextFactoryBuilder();
        final ContextualConverter<? super I, ? extends O> converter = ConverterService.getInstance().findConverter(classi, classo, defaultContextFactoryBuilder, params);
        assertNotNull("Converter not null", converter);
        ContextFactory contextFactory = defaultContextFactoryBuilder.build();
        assertEquals(o, converter.convert(i, contextFactory.newContext()));
        assertNotNull(converter.toString());
    }

    private static boolean hasZoneId(Object[] params) {
        for(Object o : params) {
            if (o instanceof TimeZone || o.getClass().getSimpleName().equals("ZoneId")) return true;
        }
        return false;
    }

}
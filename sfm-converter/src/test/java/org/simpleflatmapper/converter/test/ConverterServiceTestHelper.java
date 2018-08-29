package org.simpleflatmapper.converter.test;

import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.converter.DefaultContextFactoryBuilder;
import org.simpleflatmapper.converter.EmptyContextFactoryBuilder;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ConverterServiceTestHelper {

    @SuppressWarnings("unchecked")
    public static <I, O> void testConverter(I i, O o) throws Exception {
        testConverter(i, o, (Class<? super I>)i.getClass(), (Class<? super O>)o.getClass());
    }

    public static <I, O> void testConverter(I i, O o, Class<I> classi, Class<O> classo, Object... params) throws Exception {
        DefaultContextFactoryBuilder defaultContextFactoryBuilder = new DefaultContextFactoryBuilder();
        final Converter<? super I, ? extends O> converter = ConverterService.getInstance().findConverter(classi, classo, defaultContextFactoryBuilder, params);
        assertNotNull("Converter not null", converter);
        ContextFactory contextFactory = defaultContextFactoryBuilder.build();
        assertEquals(o, converter.convert(i, contextFactory.newContext()));
        assertNotNull(converter.toString());
    }

}
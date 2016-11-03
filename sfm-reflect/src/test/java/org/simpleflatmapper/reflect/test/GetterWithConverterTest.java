package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.reflect.getter.GetterWithConverter;

import static org.junit.Assert.assertEquals;

public class GetterWithConverterTest {

    @Test
    public void testGet() throws Exception {
        GetterWithConverter<String, Long, Integer> g =
                new GetterWithConverter<String, Long, Integer>(
                        new Converter<Long, Integer>() {

                            @Override
                            public Integer convert(Long in) throws Exception {
                                return 25;
                            }
                        },
                        new ConstantGetter<String, Long>(3l)
                );

        assertEquals(new Integer(25), g.get(null));
    }
}
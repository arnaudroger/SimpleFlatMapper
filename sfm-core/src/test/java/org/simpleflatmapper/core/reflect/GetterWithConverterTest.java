package org.simpleflatmapper.core.reflect;

import org.junit.Test;
import org.simpleflatmapper.core.reflect.getter.ConstantGetter;
import org.simpleflatmapper.core.conv.Converter;

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
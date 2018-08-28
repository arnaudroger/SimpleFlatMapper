package org.simpleflatmapper.map.impl;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.fieldmapper.FieldMapperGetter;
import org.simpleflatmapper.converter.Converter;

import static org.junit.Assert.assertEquals;

public class FieldMapperGetterWithConverterTest {

    @Test
    public void testGet() throws Exception {
        FieldMapperGetterWithConverter<String, Long, Integer> g =
                new FieldMapperGetterWithConverter<String, Long, Integer>(
                        new Converter<Long, Integer>() {

                            @Override
                            public Integer convert(Long in, Context context) throws Exception {
                                return 25;
                            }
                        },
                        new FieldMapperGetter<String, Long>() {
                            @Override
                            public Long get(String s, MappingContext<?> context) throws Exception {
                                return 3l;
                            }
                        }
                );

        assertEquals(new Integer(25), g.get(null, null));
    }
}
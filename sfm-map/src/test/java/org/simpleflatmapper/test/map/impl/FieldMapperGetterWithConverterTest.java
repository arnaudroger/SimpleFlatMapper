package org.simpleflatmapper.test.map.impl;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.map.fieldmapper.FieldMapperGetterWithConverter;

import static org.junit.Assert.assertEquals;

public class FieldMapperGetterWithConverterTest {

    @Test
    public void testGet() throws Exception {
        FieldMapperGetterWithConverter<String, Long, Integer> g =
                new FieldMapperGetterWithConverter<String, Long, Integer>(
                        new ContextualConverter<Long, Integer>() {

                            @Override
                            public Integer convert(Long in, Context context) throws Exception {
                                return 25;
                            }
                        },
                        new ContextualGetter<String, Long>() {
                            @Override
                            public Long get(String s, Context context) throws Exception {
                                return 3l;
                            }
                        }
                );

        assertEquals(new Integer(25), g.get(null, null));
    }
}
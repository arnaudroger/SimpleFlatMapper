package org.simpleflatmapper.core.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class EnumarableSpliteratorTest {


    public static final String[] STRINGS = {"str1", "str2", "str3"};
    Enumarable<String> enumarable = new StringArrayEnumarable(STRINGS);

    @Test
    public void testStreamCollect() {
        List<String> list = StreamSupport
                .stream(new EnumarableSpliterator<String>(enumarable), false)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(STRINGS), list);
    }

    @Test
    public void testStreamSkipCollect() {
        List<String> list = StreamSupport
                .stream(new EnumarableSpliterator<String>(enumarable), false)
                .limit(1)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(STRINGS).subList(0, 1), list);
    }

}
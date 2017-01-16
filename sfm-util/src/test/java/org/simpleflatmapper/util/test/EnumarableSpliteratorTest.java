package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ArrayEnumarable;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.EnumarableSpliterator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class EnumarableSpliteratorTest {


    public static final String[] STRINGS = {"str1", "str2", "str3"};
    Enumarable<String> enumarable = new ArrayEnumarable<String>(STRINGS);

    @Test
    public void testStreamCollect() {
        List<String> list = StreamSupport
                .stream(new EnumarableSpliterator<String>(enumarable), false)
                .collect(Collectors.<String>toList());
        assertEquals(Arrays.asList(STRINGS), list);
    }

    @Test
    public void testStreamSkipCollect() {
        List<String> list = StreamSupport
                .stream(new EnumarableSpliterator<String>(enumarable), false)
                .limit(1)
                .collect(Collectors.<String>toList());
        assertEquals(Arrays.asList(STRINGS).subList(0, 1), list);
    }

}
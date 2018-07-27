package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ArrayEnumerable;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.EnumerableSpliterator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class EnumerableSpliteratorTest {


    public static final String[] STRINGS = {"str1", "str2", "str3"};
    Enumerable<String> enumerable = new ArrayEnumerable<String>(STRINGS);

    @Test
    public void testStreamCollect() {
        List<String> list = StreamSupport
                .stream(new EnumerableSpliterator<String>(enumerable), false)
                .collect(Collectors.<String>toList());
        assertEquals(Arrays.asList(STRINGS), list);
    }

    @Test
    public void testStreamSkipCollect() {
        List<String> list = StreamSupport
                .stream(new EnumerableSpliterator<String>(enumerable), false)
                .limit(1)
                .collect(Collectors.<String>toList());
        assertEquals(Arrays.asList(STRINGS).subList(0, 1), list);
    }

}
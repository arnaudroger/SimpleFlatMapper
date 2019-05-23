package org.simpleflatmapper.lightningcsv.test.samples;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.CsvParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public class Issue654Test {


    @Test
    public void test() throws IOException {
        CsvParser
                .dsl().quote((char)0)
                .iterator("hello,\"salt&pepper,world")
                .forEachRemaining(strings -> Stream.of(strings).forEach(System.out::println));
    }
}

package org.simpleflatmapper.lightningcsv.test.samples;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.CsvParser;

import java.io.IOException;

public class Issue654Test {


    //IFJAVA8_START
    @Test
    public void test() throws IOException {
        CsvParser
                .dsl().quote((char)0)
                .iterator("hello,\"salt&pepper,world")
                .forEachRemaining(strings -> java.util.stream.Stream.of(strings).forEach(System.out::println));
    }
    //IFJAVA8_END
}

package org.simpleflatmapper.sandbox;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvParser;

public class CsvParsingTest {


    @Test
    public void test() throws Exception {
        CsvParser.dsl().rowIterator("a\nb").forEachRemaining(System.out::println);
    }
}

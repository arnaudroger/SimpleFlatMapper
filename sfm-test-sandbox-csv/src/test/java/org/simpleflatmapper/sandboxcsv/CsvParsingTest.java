package org.simpleflatmapper.sandboxcsv;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvParser;

import static org.junit.Assert.assertEquals;

public class CsvParsingTest {


    public static class MyObject {
        private final String name;
        private final String value;

        public MyObject(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
    @Test
    public void testRow() throws Exception {
        CsvParser.dsl().rowIterator("a\nb").forEachRemaining(System.out::println);
    }
    @Test
    public void testMap() throws Exception {
        MyObject o = CsvParser.dsl().mapTo(MyObject.class).iterator("name,value\nnn,vv").next();

        assertEquals("nn", o.name);
        assertEquals("vv", o.value);

    }
}

package org.simpleflatmapper.lombok;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvParser;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class MyObjectMapperTest {

    @Test
    public void testMapLombokObject() throws IOException {
        MyObject object =
                CsvParser
                        .mapTo(MyObject.class)
                        .iterator(new StringReader("id,name\n123,n1")).next();

        assertEquals(123, object.getId());
        assertEquals("n1", object.getName());
    }
}

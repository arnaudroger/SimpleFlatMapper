package org.simpleflatmapper.csv.test.samples;

import org.jooq.lambda.tuple.Tuple3;
import org.junit.Test;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.util.TypeReference;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class JoolTupleTest {

    @Test
    public void testCsvParser() throws IOException {
        final CsvParser.StaticMapToDSL<Tuple3<Long, Integer, Short>> mapToDSL = CsvParser.mapTo(new TypeReference<Tuple3<Long, Integer, Short>>() {
        }).defaultHeaders();
        final Iterator<Tuple3<Long, Integer, Short>> iterator = mapToDSL.iterator(new StringReader("6,7,3\n7,8,9"));

        final Tuple3<Long, Integer, Short> tuple1 = iterator.next();

        assertEquals(6l, tuple1.v1().longValue());
        assertEquals(7, tuple1.v2().intValue());
        assertEquals((short)3, tuple1.v3().shortValue());

        final Tuple3<Long, Integer, Short> tuple2 = iterator.next();

        assertEquals(7l, tuple2.v1().longValue());
        assertEquals(8, tuple2.v2().intValue());
        assertEquals((short)9, tuple2.v3().shortValue());
    }

}

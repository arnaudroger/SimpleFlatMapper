package org.simpleflatmapper.csv.test.samples;

import com.boundary.tuple.FastTuple;
import com.boundary.tuple.TupleSchema;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.csv.CsvParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class FastTupleTest {

    private FastTuple tuple;

    @Before
    public void setUp() throws Exception {
        TupleSchema schema = TupleSchema.builder().
                addField("fieldA", Long.TYPE).
                addField("fieldB", Integer.TYPE).
                addField("fieldC", Short.TYPE).
                heapMemory().
                build();

        tuple = schema.createTuple();
    }


    @Test
    public void testCsvParser() throws IOException {
        final CsvParser.StaticMapToDSL<? extends FastTuple> mapToDSL = CsvParser.mapTo(tuple.getClass()).defaultHeaders();
        final Iterator<? extends FastTuple> iterator = mapToDSL.iterator(new StringReader("6,7,3\n7,8,9"));


        final FastTuple tuple1 = iterator.next();
        final FastTuple tuple2 = iterator.next();


        assertEquals(6l, tuple1.getLong(1));
        assertEquals(7, tuple1.getInt(2));
        assertEquals((short)3, tuple1.getShort(3));


        assertEquals(7l, tuple2.getLong(1));
        assertEquals(8, tuple2.getInt(2));
        assertEquals((short)9, tuple2.getShort(3));
    }
}

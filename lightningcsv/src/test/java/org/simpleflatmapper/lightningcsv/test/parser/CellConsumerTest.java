package org.simpleflatmapper.lightningcsv.test.parser;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CellConsumerTest {

    //IFJAVA8_START

    @Test
    public void testCellConsumerofConsumer() {
        List<String> cells = new ArrayList<String>();
        CellConsumer cellConsumer = CellConsumer.of(cells::add);

        assertTrue(cellConsumer.endOfRow());
        cellConsumer.end();
        cellConsumer.newCell("hello".toCharArray(), 1, 2);

        assertArrayEquals(new String[] { "el"}, cells.toArray(new String[0]));
    }

    //IFJAVA8_END

}
package org.simpleflatmapper.lightningcsv.test.parser;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.parser.IgnoreCellConsumer;

import static org.junit.Assert.*;

public class IgnoreCellConsumerTest {

    @Test
    public void testIgnore() {
        IgnoreCellConsumer.INSTANCE.newCell(null, -1, -1);
        assertFalse(IgnoreCellConsumer.INSTANCE.endOfRow());
        IgnoreCellConsumer.INSTANCE.end();
    }

}
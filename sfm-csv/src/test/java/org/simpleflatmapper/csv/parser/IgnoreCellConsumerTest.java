package org.simpleflatmapper.csv.parser;

import org.junit.Test;

import static org.junit.Assert.*;

public class IgnoreCellConsumerTest {

    @Test
    public void testIgnore() {
        IgnoreCellConsumer.INSTANCE.newCell(null, -1, -1);
        assertFalse(IgnoreCellConsumer.INSTANCE.endOfRow());
        IgnoreCellConsumer.INSTANCE.end();
    }

}
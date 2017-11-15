package org.simpleflatmapper.csv.parser;

import org.junit.Test;

import static org.junit.Assert.*;

public class CharConsumerTest {
    @Test
    public void packUnpack() throws Exception {
        checkPackUnpack(456, 67);
        checkPackUnpack(Integer.MAX_VALUE, Integer.MAX_VALUE);
        checkPackUnpack(Integer.MIN_VALUE, Integer.MIN_VALUE);
        checkPackUnpack(-1, -1);
        checkPackUnpack(1, 1);
    }

    private void checkPackUnpack(int index, int state) {
        long p = CharConsumer.pack(index, state);
        assertEquals(index, CharConsumer.unpackIndex(p));
        assertEquals(state, CharConsumer.unpackState(p));
    }

    

}
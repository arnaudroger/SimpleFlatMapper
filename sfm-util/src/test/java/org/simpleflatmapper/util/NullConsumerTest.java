package org.simpleflatmapper.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class NullConsumerTest {

    @Test
    public void testNullConsumer() {
        NullConsumer.INSTANCE.accept(null);
    }
}
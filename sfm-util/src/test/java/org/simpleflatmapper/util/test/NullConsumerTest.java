package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.NullConsumer;

import static org.junit.Assert.*;

public class NullConsumerTest {

    @Test
    public void testNullConsumer() {
        NullConsumer.INSTANCE.accept(null);
    }
}
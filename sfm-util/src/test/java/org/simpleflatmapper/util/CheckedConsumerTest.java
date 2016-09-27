package org.simpleflatmapper.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CheckedConsumerTest {

    //IFJAVA8_START
    @Test
    public void toConsumer() throws Exception {
        try {
            checkedConsumer();
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }
    }

    private void checkedConsumer() {
        CheckedConsumer.toConsumer((t) -> {
            throw new IOException();
        }).accept(null);
    }
    //IFJAVA8_END


}
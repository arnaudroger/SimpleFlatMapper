package org.simpleflatmapper.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CheckedConsumerHelperTest {
    @Test
    public void toConsumer() throws Exception {
        Consumer<Object> objectConsumer = CheckedConsumerHelper.toConsumer(new CheckedConsumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                throw new IOException();
            }
        });

        try {
            run(objectConsumer);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }
    }

    private void run(Consumer<Object> objectConsumer) {
        objectConsumer.accept(null);
    }

}
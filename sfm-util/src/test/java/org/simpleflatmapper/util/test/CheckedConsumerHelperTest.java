package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.CheckedConsumerHelper;
import org.simpleflatmapper.util.Consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CheckedConsumerHelperTest {
    @Test
    public void toConsumerThrowException() throws Exception {
        Consumer<Object> objectConsumer = CheckedConsumerHelper.toConsumer(new CheckedConsumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                throw new IOException();
            }
        });

        try {
            run(objectConsumer, null);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }
    }

    @Test
    public void toConsumerDelegate() throws Exception {
        final List<String> strings = new ArrayList<String>();
        Consumer<String> objectConsumer = CheckedConsumerHelper.toConsumer(new CheckedConsumer<String>() {
            @Override
            public void accept(String o) throws Exception {
                strings.add(o);
            }
        });

        run(objectConsumer, "hello");
        assertArrayEquals(new String[] {"hello"}, strings.toArray(new String[0]));
    }

    private <T> void run(Consumer<T> objectConsumer, T value) {
        objectConsumer.accept(value);
    }

}
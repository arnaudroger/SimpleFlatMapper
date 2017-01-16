package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.CloseableIterator;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CloseableIteratorTest {

    @Test
    public void test() throws IOException {
        MyCloseable closeable = new MyCloseable();
        List<String> expected = Arrays.asList("str1", "str2");
        CloseableIterator<String> closeableIterator =
                new CloseableIterator<String>(expected.iterator(), closeable);

        try {
            List<String> list = new ArrayList<String>();

            while(closeableIterator.hasNext()) {
                list.add(closeableIterator.next());

                try {
                    closeableIterator.remove();
                    fail();
                } catch (UnsupportedOperationException e){
                }
            }

            assertEquals(expected, list);
        } finally {
            closeableIterator.close();
        }

        assertEquals(1, closeable.closed);



    }

    private class MyCloseable implements Closeable {
        private int closed;

        @Override
        public void close() throws IOException {
            closed++;
        }
    }
}
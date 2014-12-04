package org.sfm.csv.parser;

import org.junit.Test;
import org.sfm.utils.RowHandler;

import static org.junit.Assert.*;

public class StringArrayConsumerTest {

    static class MyRowHandler implements RowHandler<String[]> {

        private String[] strings;

        @Override
        public void handle(String[] strings) throws Exception {
            this.strings = strings;
        }
    }
    @Test
    public void testNewCell() throws Exception {
        StringArrayConsumer<MyRowHandler> consumer = new StringArrayConsumer<MyRowHandler>(new MyRowHandler());
        for(int i = 0; i < 20; i ++) {
            char[] chars = Integer.toString(i).toCharArray();
            consumer.newCell(chars, 0 , chars.length);
        }

        consumer.end();

        String[] str = consumer.handler().strings;
        assertEquals(20, str.length);
        for(int i = 0; i < 20; i ++) {
            assertEquals(Integer.toString(i), str[i]);
        }
    }
}
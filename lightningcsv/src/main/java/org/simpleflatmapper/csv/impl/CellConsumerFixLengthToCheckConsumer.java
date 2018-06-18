package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.parser.CellConsumer;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ErrorHelper;

import java.util.Arrays;

public class CellConsumerFixLengthToCheckConsumer implements CellConsumer {
    
    private final String[] values;
    private int currentIndex;
    private final Consumer<String[]> consumer;

    public CellConsumerFixLengthToCheckConsumer(int l, Consumer<String[]> consumer) {
        values = new String[l];
        this.consumer = consumer;
    }
    @Override
    public void newCell(char[] chars, int offset, int length) {
        if (currentIndex < values.length) {
            values[currentIndex++] = new String(chars, offset, length);
        }
    }

    @Override
    public boolean endOfRow() {
        try {
            consumer.accept(Arrays.copyOf(values, values.length));
        } finally {
            Arrays.fill(values, null);
            currentIndex = 0;
        }
        return true;
    }

    @Override
    public void end() {
        if (currentIndex > 0) {
            endOfRow();
        }
    }
}

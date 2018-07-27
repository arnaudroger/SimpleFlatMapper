package org.simpleflatmapper.lightningcsv.impl;

import org.simpleflatmapper.lightningcsv.parser.CellConsumer;

import java.util.Arrays;

public class CellConsumerCapture implements CellConsumer {
    
    private String[] values = new String[10];
    private int currentIndex;
    private boolean endOfRow = false;
    
    @Override
    public void newCell(char[] chars, int offset, int length) {
        if (currentIndex >= values.length) {
            values = Arrays.copyOf(values, values.length * 2);
        }
        values[currentIndex++] = new String(chars, offset, length);
    }

    @Override
    public boolean endOfRow() {
        endOfRow = true;
        return true;
    }

    @Override
    public void end() {
        endOfRow = true;
    }
    
    public String[] values() {
        String[] valuesCopy = Arrays.copyOf(values, currentIndex);
        Arrays.fill(values, 0, currentIndex, null);
        currentIndex = 0;
        endOfRow = false;
        return valuesCopy;
    }

    public boolean hasData() {
        return endOfRow;
    }
}

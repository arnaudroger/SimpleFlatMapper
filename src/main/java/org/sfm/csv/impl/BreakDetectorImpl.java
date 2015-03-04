package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;

import java.util.Arrays;

public class BreakDetectorImpl implements BreakDetector {
    private final CsvColumnKey[] keys;

    private boolean brokenCheck;
    private Object[] lastKeys;
    private boolean broken;

    public BreakDetectorImpl(CsvColumnKey[] keys) {
        this.keys = keys;
    }

    @Override
    public boolean isBroken(DelayedCellSetter<?, ?>[] delayedCellSetters) {
        if (!brokenCheck) {
            updateStatus(delayedCellSetters);
            return broken;
        }
        return false;
    }

    private void updateStatus(DelayedCellSetter<?, ?>[] delayedCellSetters) {
        Object[] currentKeys = getKeys(delayedCellSetters);
        broken = lastKeys == null || !Arrays.equals(currentKeys, lastKeys);
        lastKeys = currentKeys;
        brokenCheck = true;
    }

    private Object[] getKeys(DelayedCellSetter<?, ?>[] delayedCellSetters) {
        Object[] currentKeys = new Object[keys.length];
        for(int i = 0; i < keys.length ; i++) {
            currentKeys[i] = delayedCellSetters[keys[i].getIndex()].peekValue();
        }
        return currentKeys;
    }

    @Override
    public void reset() {
        brokenCheck = false;
        broken = false;
    }
}

package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.CsvColumnKey;

import java.util.Arrays;

public class BreakDetector  {
    private final CsvColumnKey[] keys;
    private final BreakDetector parent;
    private final int lastIndex;

    private boolean brokenCheck;
    private Object[] lastKeys;
    private boolean broken;
    private boolean isNotNull = true;

    public BreakDetector(CsvColumnKey[] keys, BreakDetector parent, int delayedSetterEnd) {
        this.keys = keys;
        this.parent = parent;
        this.lastIndex = Math.max(delayedSetterEnd, getLastIndex(keys, parent));
    }

    private int getLastIndex(CsvColumnKey[] keys, BreakDetector parent) {
        if (keys.length == 0) return parent.lastIndex;

        int i = 0;
        for(CsvColumnKey k : keys) {
            i = Math.max(i, k.getIndex());
        }
        return i;
    }

    public boolean updateStatus(CsvMapperCellHandler<?> mapperSetters, int cellIndex) {
        if (cellIndex == lastIndex) {
            if (brokenCheck) {
                throw new IllegalStateException();
            }
            if (keys.length > 0) {
                Object[] currentKeys = getKeys(mapperSetters);
                broken = lastKeys == null || !Arrays.equals(currentKeys, lastKeys);
                lastKeys = currentKeys;
            }
            brokenCheck = true;
            return true;
        }
        return false;
    }

    public boolean broken() {
        statusCheck();
        return broken || (parent != null && parent.broken());
    }

    private void statusCheck() {
        if (!brokenCheck) {
            throw new IllegalStateException();
        }
    }

    public boolean isNotNull() {
        if (keys.length == 0) {
            return true;
        } else {
            statusCheck();
            return isNotNull;
        }
    }

    private Object[] getKeys(CsvMapperCellHandler<?> mapperSetters) {
        isNotNull = true;
        Object[] currentKeys = new Object[keys.length];
        for(int i = 0; i < keys.length ; i++) {
            final Object o = mapperSetters.peekDelayedCellSetterValue(keys[i]);
            isNotNull = isNotNull && o != null;
            currentKeys[i] = o;
        }
        return currentKeys;
    }

    public void reset() {
        brokenCheck = false;
        broken = false;
    }
}

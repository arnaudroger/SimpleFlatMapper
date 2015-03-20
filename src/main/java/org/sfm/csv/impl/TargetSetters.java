package org.sfm.csv.impl;


import org.sfm.csv.CsvColumnKey;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.ForEachIndexedCallBack;

public class TargetSetters<T> {

    /**
     * mapping information
     */
    protected final Instantiator<TargetSetters<T>, T> instantiator;
    protected final DelayedCellSetter<T, ?>[] delayedCellSetters;
    protected final CellSetter<T>[] setters;
    protected final CsvColumnKey[] columns;
    protected final int totalLength;


    public TargetSetters(Instantiator<TargetSetters<T>, T> instantiator, DelayedCellSetter<T, ?>[] delayedCellSetters, CellSetter<T>[] setters, CsvColumnKey[] columns) {
        this.instantiator = instantiator;
        this.delayedCellSetters = delayedCellSetters;
        this.setters = setters;
        this.columns = columns;
        this.totalLength = delayedCellSetters.length + setters.length;
    }


    public final CsvColumnKey getColumn(int cellIndex) {
        for (CsvColumnKey key : columns) {
            if (key.getIndex() == cellIndex) {
                return key;
            }
        }
        return null;
    }

    public void forEachSettableDelayedSetters(ForEachIndexedCallBack<DelayedCellSetter<T, ?>> callBack) {
        for (int i = 0; i < delayedCellSetters.length; i++) {
            DelayedCellSetter<T, ?> delayedSetter = delayedCellSetters[i];
            if (delayedSetter != null && delayedSetter.isSettable()) {
                callBack.handle(delayedSetter, i);
            }
        }
    }

    public final T createInstance() {
        try {
            return instantiator.newInstance(this);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    public boolean isDelayedSetter(int cellIndex) {
        return cellIndex < delayedCellSetters.length;
    }

    public DelayedCellSetter<T, ?> getDelayedCellSetter(CsvColumnKey key) {
        final int index = key.getIndex();
        return getDelayedCellSetter(index);
    }

    public DelayedCellSetter<T, ?> getDelayedCellSetter(int index) {
        return delayedCellSetters[index];
    }

    public CellSetter<T> getCellSetter(int cellIndex) {
        final int i = cellIndex - delayedCellSetters.length;
        if (i < setters.length) {
            return setters[i];
        } else {
            return null;
        }
    }
}

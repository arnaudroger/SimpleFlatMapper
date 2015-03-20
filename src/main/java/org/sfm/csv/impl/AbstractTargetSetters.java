package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.ForEachIndexedCallBack;

/**
 * Created by aroger on 20/03/15.
 */
public abstract class AbstractTargetSetters<T> {
    protected final Instantiator<TargetSetters<T>, T> instantiator;
    protected final CsvColumnKey[] columns;
    protected final int totalLength;
    protected final int delayedCellSettersLength;

    public AbstractTargetSetters(Instantiator<TargetSetters<T>, T> instantiator, CsvColumnKey[] columns) {
        this.instantiator = instantiator;
        this.columns = columns;
        this.totalLength = delayedCellSetters.length + setters.length;
        this.delayedCellSettersLength = delayedCellSetters.length;
    }

    public final CsvColumnKey getColumn(int cellIndex) {
        for (CsvColumnKey key : columns) {
            if (key.getIndex() == cellIndex) {
                return key;
            }
        }
        return null;
    }

    public abstract void forEachSettableDelayedSetters(ForEachIndexedCallBack<DelayedCellSetter<T, ?>> callBack);

    public final T createInstance() {
        try {
            return instantiator.newInstance(this);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    public boolean isDelayedSetter(int cellIndex) {
        return cellIndex < delayedCellSettersLength;
    }

    public DelayedCellSetter<T, ?> getDelayedCellSetter(CsvColumnKey key) {
        final int index = key.getIndex();
        return getDelayedCellSetter(index);
    }

    public abstract DelayedCellSetter<T, ?> getDelayedCellSetter(int index);

    public abstract CellSetter<T> getCellSetter(int cellIndex);
}

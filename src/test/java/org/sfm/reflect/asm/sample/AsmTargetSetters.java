package org.sfm.reflect.asm.sample;


import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.impl.AbstractTargetSetters;
import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.ForEachIndexedCallBack;

public class AsmTargetSetters<T> extends AbstractTargetSetters<T> {

    /**
     * mapping information
     */
    protected final DelayedCellSetter<T, ?>[] delayedCellSetters;
    protected final CellSetter<T>[] setters;


    public AsmTargetSetters(Instantiator<AbstractTargetSetters<T>, T> instantiator, DelayedCellSetter<T, ?>[] delayedCellSetters, CellSetter<T>[] setters, CsvColumnKey[] columns) {
        super(instantiator, columns, delayedCellSetters.length, setters.length);
        this.delayedCellSetters = delayedCellSetters;
        this.setters = setters;
    }


    @Override
    public void forEachSettableDelayedSetters(ForEachIndexedCallBack<DelayedCellSetter<T, ?>> callBack) {
        for (int i = 0; i < delayedCellSetters.length; i++) {
            DelayedCellSetter<T, ?> delayedSetter = delayedCellSetters[i];
            if (delayedSetter != null && delayedSetter.isSettable()) {
                callBack.handle(delayedSetter, i);
            }
        }
    }

    @Override
    public DelayedCellSetter<T, ?> getDelayedCellSetter(int index) {
        return delayedCellSetters[index];
    }

    @Override
    public CellSetter<T> getCellSetter(int cellIndex) {
        final int i = cellIndex - delayedCellSetters.length;
        if (i < setters.length) {
            return setters[i];
        } else {
            return null;
        }
    }
}

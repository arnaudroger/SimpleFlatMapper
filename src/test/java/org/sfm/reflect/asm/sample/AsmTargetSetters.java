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
    protected final DelayedCellSetter<T, ?> delayedCellSetter0;
    protected final DelayedCellSetter<T, ?> delayedCellSetter1;
    protected final CellSetter<T> setter2;
    protected final CellSetter<T> setter3;


    public AsmTargetSetters(Instantiator<AbstractTargetSetters<T>, T> instantiator, DelayedCellSetter<T, ?>[] delayedCellSetters, CellSetter<T>[] setters, CsvColumnKey[] columns) {
        super(instantiator, columns, delayedCellSetters.length, setters.length);
        delayedCellSetter0 = delayedCellSetters[0];
        delayedCellSetter1 = delayedCellSetters[1];

        setter2 = setters[0];
        setter3 = setters[1];
    }


    @Override
    public void forEachSettableDelayedSetters(ForEachIndexedCallBack<DelayedCellSetter<T, ?>> callBack) {
        callBack.handle(delayedCellSetter0, 0);
        callBack.handle(delayedCellSetter1, 1);
    }

    @Override
    public DelayedCellSetter<T, ?> getDelayedCellSetter(int index) {
        DelayedCellSetter<T, ?> setter;
        switch(index) {
            case 0:
                setter = delayedCellSetter0;
            case 1:
                setter = delayedCellSetter1;
            case 2:
                setter = delayedCellSetter1;
            default: setter = null;
        }

        return setter;

    }

    @Override
    public CellSetter<T> getCellSetter(int index) {
        switch(index) {
            case 2:
                return setter2;
            case 3:
                return setter3;
            case 4:
                return setter3;
            default:
                return null;
        }

    }
}

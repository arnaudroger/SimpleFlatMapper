package org.sfm.csv.impl;


import org.sfm.csv.CsvColumnKey;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.Instantiator;

public class CsvCellHandlerImpl<T> extends CsvCellHandler<T> {

    /**
     * mapping information
     */
    protected final DelayedCellSetter<T, ?>[] delayedCellSetters;
    protected final CellSetter<T>[] setters;


    public CsvCellHandlerImpl(Instantiator<CsvCellHandler<T>, T> instantiator, DelayedCellSetter<T, ?>[] delayedCellSetters,
                              CellSetter<T>[] setters, CsvColumnKey[] columns,
                              ParsingContext parsingContext, FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler) {
        super(instantiator, columns, delayedCellSetters.length, setters.length, parsingContext, fieldErrorHandler);
        this.delayedCellSetters = delayedCellSetters;
        this.setters = setters;
    }

    @Override
    public void applyDelayedSetters() {
        for (int i = 0; i < delayedCellSetters.length; i++) {
            DelayedCellSetter<T, ?> delayedSetter = delayedCellSetters[i];
            if (delayedSetter != null && delayedSetter.isSettable()) {
                try {
                    delayedSetter.set(currentInstance);
                } catch (Exception e) {
                    fieldErrorHandler.errorMappingField(getColumn(i), this, currentInstance, e);
                }
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

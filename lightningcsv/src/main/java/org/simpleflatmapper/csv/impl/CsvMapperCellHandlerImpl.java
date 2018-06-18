package org.simpleflatmapper.csv.impl;


import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.reflect.Instantiator;

public final class CsvMapperCellHandlerImpl<T> extends CsvMapperCellHandler<T> {

    /**
     * mapping information
     */
    protected final DelayedCellSetter<T, ?>[] delayedCellSetters;
    protected final CellSetter<T>[] setters;


    public CsvMapperCellHandlerImpl(Instantiator<CsvMapperCellHandler<T>, T> instantiator, DelayedCellSetter<T, ?>[] delayedCellSetters,
                                    CellSetter<T>[] setters, CsvColumnKey[] columns,
                                    ParsingContext parsingContext, FieldMapperErrorHandler<? super CsvColumnKey> fieldErrorHandler) {
        super(instantiator, columns, delayedCellSetters.length, setters.length, parsingContext, fieldErrorHandler);
        this.delayedCellSetters = delayedCellSetters;
        this.setters = setters;
    }

    @Override
    public void delayedCellValue(char[] chars, int offset, int length, int cellIndex) {
        try {
            DelayedCellSetter<T, ?> delayedCellSetter = delayedCellSetters[cellIndex];
            if (delayedCellSetter != null) {
                delayedCellSetter.set(chars, offset, length, parsingContext);
            }
        } catch (Exception e) {
            fieldError(cellIndex, e);
        }
    }

    @Override
    public void cellValue(char[] chars, int offset, int length, int cellIndex) {
        try {
            CellSetter<T> cellSetter = getCellSetter(cellIndex);
            if (cellSetter != null) {
                cellSetter.set(currentInstance, chars, offset, length, parsingContext);
            }
        } catch (Exception e) {
            fieldError(cellIndex, e);
        }
    }

    @Override
    public void applyDelayedSetters() {
        for (int i = 0; i < delayedCellSetters.length; i++) {
            DelayedCellSetter<T, ?> delayedSetter = delayedCellSetters[i];
            if (delayedSetter != null && delayedSetter.isSettable()) {
                try {
                    delayedSetter.set(currentInstance);
                } catch (Exception e) {
                    fieldError(i, e);
                }
            }
        }

    }

    @Override
    public DelayedCellSetter<T, ?> getDelayedCellSetter(int index) {
        return delayedCellSetters[index];
    }

    @Override
    public final Object peekDelayedCellSetterValue(CsvColumnKey key) {
        return delayedCellSetters[key.getIndex()].peekValue();
    }

    private CellSetter<T> getCellSetter(int cellIndex) {
        final int i = cellIndex - delayedCellSetters.length;
        if (i < setters.length) {
            return setters[i];
        } else {
            return null;
        }
    }
}

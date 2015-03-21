package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.ErrorHelper;

public abstract class CsvCellHandler<T> {
    protected final Instantiator<CsvCellHandler<T>, T> instantiator;
    protected final CsvColumnKey[] columns;
    protected final int totalLength;
    protected final int delayedCellSettersLength;
    protected final ParsingContext parsingContext;

    protected final FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler;


    protected T currentInstance;

    public CsvCellHandler(Instantiator<CsvCellHandler<T>, T> instantiator,
                          CsvColumnKey[] columns, int delayedCellSettersLength, int cellSettersLength,
                          ParsingContext parsingContext, FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler) {
        this.instantiator = instantiator;
        this.columns = columns;
        this.parsingContext = parsingContext;
        this.fieldErrorHandler = fieldErrorHandler;
        this.totalLength = delayedCellSettersLength + cellSettersLength;
        this.delayedCellSettersLength = delayedCellSettersLength;
    }

    public final CsvColumnKey getColumn(int cellIndex) {
        for (CsvColumnKey key : columns) {
            if (key.getIndex() == cellIndex) {
                return key;
            }
        }
        return null;
    }

    public void newCellForDelayedSetter(char[] chars, int offset, int length, int cellIndex) {
        try {
            DelayedCellSetter<T, ?> delayedCellSetter = getDelayedCellSetter(cellIndex);
            if (delayedCellSetter != null) {
                delayedCellSetter.set(chars, offset, length, parsingContext);
            }
        } catch (Exception e) {
            fieldErrorHandler.errorMappingField(getColumn(cellIndex), this, currentInstance, e);
        }
    }

    public void newCellForSetter(char[] chars, int offset, int length, int cellIndex) {
        try {
            CellSetter<T> cellSetter = getCellSetter(cellIndex);
            if (cellSetter != null) {
                cellSetter.set(currentInstance, chars, offset, length, parsingContext);
            }
        } catch (Exception e) {
            fieldErrorHandler.errorMappingField(getColumn(cellIndex), this, currentInstance, e);
        }
    }

    public abstract void applyDelayedSetters();

    public final void createInstance() {
        try {
            currentInstance = instantiator.newInstance(this);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
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

    public T getCurrentInstance() {
        return currentInstance;
    }

    public void createInstanceIfNull() {
        if (currentInstance == null) {
            createInstance();
        }
    }

    public void resetCurrentInstance() {
        currentInstance = null;
    }

    public boolean hasInstance() {
        return currentInstance != null;
    }


}

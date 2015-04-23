package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.ErrorHelper;

public abstract class CsvMapperCellHandler<T> {
    protected final Instantiator<CsvMapperCellHandler<T>, T> instantiator;
    protected final CsvColumnKey[] columns;
    protected final int totalLength;
    protected final int delayedCellSettersLength;
    protected final ParsingContext parsingContext;

    protected final FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler;


    protected T currentInstance;

    public CsvMapperCellHandler(Instantiator<CsvMapperCellHandler<T>, T> instantiator,
                                CsvColumnKey[] columns, int delayedCellSettersLength, int cellSettersLength,
                                ParsingContext parsingContext, FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler) {
        this.instantiator = instantiator;
        this.columns = columns;
        this.parsingContext = parsingContext;
        this.fieldErrorHandler = fieldErrorHandler;
        this.totalLength = delayedCellSettersLength + cellSettersLength;
        this.delayedCellSettersLength = delayedCellSettersLength;
    }

    public abstract void delayedCellValue(CharSequence value, int cellIndex);

    public abstract void cellValue(CharSequence value, int cellIndex);

    public abstract void applyDelayedSetters();

    public abstract DelayedCellSetter<T, ?> getDelayedCellSetter(int index);

    public abstract Object peekDelayedCellSetterValue(CsvColumnKey key);


    public final void createInstance() {
        try {
            currentInstance = instantiator.newInstance(this);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }
    }

    public final boolean isDelayedSetter(int cellIndex) {
        return cellIndex < delayedCellSettersLength;
    }


    protected final void fieldError(int cellIndex, Exception e) {
        fieldErrorHandler.errorMappingField(getColumn(cellIndex), this, currentInstance, e);
    }

    private CsvColumnKey getColumn(int cellIndex) {
        for (CsvColumnKey key : columns) {
            if (key.getIndex() == cellIndex) {
                return key;
            }
        }
        return null;
    }

    public final T getCurrentInstance() {
        return currentInstance;
    }

    public final void createInstanceIfNull() {
        if (currentInstance == null) {
            createInstance();
        }
    }

    public final void resetCurrentInstance() {
        currentInstance = null;
    }

    public final boolean hasInstance() {
        return currentInstance != null;
    }
}

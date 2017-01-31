package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.util.ErrorHelper;

public abstract class CsvMapperCellHandler<T> {
    protected final Instantiator<CsvMapperCellHandler<T>, T> instantiator;
    protected final CsvColumnKey[] columns;
    protected final int totalLength;
    protected final int delayedCellSettersLength;
    protected final ParsingContext parsingContext;

    protected final FieldMapperErrorHandler<? super CsvColumnKey> fieldErrorHandler;


    protected T currentInstance;

    public CsvMapperCellHandler(Instantiator<CsvMapperCellHandler<T>, T> instantiator,
                                CsvColumnKey[] columns, int delayedCellSettersLength, int cellSettersLength,
                                ParsingContext parsingContext, FieldMapperErrorHandler<? super CsvColumnKey> fieldErrorHandler) {
        this.instantiator = instantiator;
        this.columns = columns;
        this.parsingContext = parsingContext;
        this.fieldErrorHandler = fieldErrorHandler;
        this.totalLength = delayedCellSettersLength + cellSettersLength;
        this.delayedCellSettersLength = delayedCellSettersLength;
    }

    public abstract void delayedCellValue(char[] chars, int offset, int length, int cellIndex);

    public abstract void cellValue(char[] chars, int offset, int length, int cellIndex);

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
        if (fieldErrorHandler == null) {
            ErrorHelper.rethrow(e);
        }
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

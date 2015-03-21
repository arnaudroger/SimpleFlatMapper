package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.Instantiator;

public class CsvCellHandlerFactory<T> {

    protected final Instantiator<CsvCellHandler<T>, T> instantiator;
    protected final CsvColumnKey[] keys;
    protected final ParsingContextFactory parsingContextFactory;
    protected final FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler;

    public CsvCellHandlerFactory(Instantiator<CsvCellHandler<T>, T> instantiator, CsvColumnKey[] keys, ParsingContextFactory parsingContextFactory, FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler) {
        this.instantiator = instantiator;
        this.keys = keys;
        this.parsingContextFactory = parsingContextFactory;
        this.fieldErrorHandler = fieldErrorHandler;
    }

    public CsvCellHandler<T> newInstace(DelayedCellSetter<T, ?>[] delayedSetters, CellSetter<T>[] setters) {
        return new CsvCellHandlerImpl(instantiator, delayedSetters, setters, keys, parsingContextFactory.newContext(), fieldErrorHandler);
    }

    @Override
    public String toString() {
        return "TargetSettersFactory{" +
                "instantiator=" + instantiator +
                '}';
    }
}

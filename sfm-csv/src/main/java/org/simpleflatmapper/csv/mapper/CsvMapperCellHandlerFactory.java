package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.ParsingContextFactory;
import org.simpleflatmapper.csv.impl.CsvMapperCellHandlerImpl;
import org.simpleflatmapper.core.map.FieldMapperErrorHandler;
import org.simpleflatmapper.core.reflect.Instantiator;

public class CsvMapperCellHandlerFactory<T> {

    protected final Instantiator<CsvMapperCellHandler<T>, T> instantiator;
    protected final CsvColumnKey[] keys;
    protected final ParsingContextFactory parsingContextFactory;
    protected final FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler;

    public CsvMapperCellHandlerFactory(Instantiator<CsvMapperCellHandler<T>, T> instantiator, CsvColumnKey[] keys, ParsingContextFactory parsingContextFactory, FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler) {
        this.instantiator = instantiator;
        this.keys = keys;
        this.parsingContextFactory = parsingContextFactory;
        this.fieldErrorHandler = fieldErrorHandler;
    }

    public CsvMapperCellHandler<T> newInstance(DelayedCellSetter<T, ?>[] delayedSetters, CellSetter<T>[] setters) {
        return new CsvMapperCellHandlerImpl<T>(instantiator, delayedSetters, setters, keys, parsingContextFactory.newContext(), fieldErrorHandler);
    }

    @Override
    public String toString() {
        return "TargetSettersFactory{" +
                "instantiator=" + instantiator +
                '}';
    }
}

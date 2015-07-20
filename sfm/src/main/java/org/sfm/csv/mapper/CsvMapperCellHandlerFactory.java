package org.sfm.csv.mapper;

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.ParsingContextFactory;
import org.sfm.csv.impl.CsvMapperCellHandlerImpl;
import org.sfm.csv.mapper.CellSetter;
import org.sfm.csv.mapper.CsvMapperCellHandler;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.Instantiator;

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

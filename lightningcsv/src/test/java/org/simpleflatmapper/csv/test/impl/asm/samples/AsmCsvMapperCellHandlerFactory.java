package org.simpleflatmapper.csv.test.impl.asm.samples;

import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.ParsingContextFactory;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandlerFactory;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.reflect.Instantiator;

public class AsmCsvMapperCellHandlerFactory extends CsvMapperCellHandlerFactory<DbObject> {
    public AsmCsvMapperCellHandlerFactory(Instantiator<CsvMapperCellHandler<DbObject>, DbObject> instantiator, CsvColumnKey[] keys, ParsingContextFactory parsingContextFactory, FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler) {
        super(instantiator, keys, parsingContextFactory, fieldErrorHandler);
    }

    @Override
    public CsvMapperCellHandler<DbObject> newInstance(DelayedCellSetter<DbObject, ?>[] delayedSetters, CellSetter<DbObject>[] setters) {
        return new AsmCsvMapperCellHandler(instantiator, delayedSetters, setters, keys, parsingContextFactory.newContext(), fieldErrorHandler);
    }
}

package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.ParsingContextFactory;
import org.sfm.csv.mapper.CellSetter;
import org.sfm.csv.mapper.CsvMapperCellHandler;
import org.sfm.csv.mapper.CsvMapperCellHandlerFactory;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.Instantiator;

public class AsmCsvMapperCellHandlerFactory extends CsvMapperCellHandlerFactory<DbObject> {
    public AsmCsvMapperCellHandlerFactory(Instantiator<CsvMapperCellHandler<DbObject>, DbObject> instantiator, CsvColumnKey[] keys, ParsingContextFactory parsingContextFactory, FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler) {
        super(instantiator, keys, parsingContextFactory, fieldErrorHandler);
    }

    @Override
    public CsvMapperCellHandler<DbObject> newInstance(DelayedCellSetter<DbObject, ?>[] delayedSetters, CellSetter<DbObject>[] setters) {
        return new AsmCsvMapperCellHandler(instantiator, delayedSetters, setters, keys, parsingContextFactory.newContext(), fieldErrorHandler);
    }
}

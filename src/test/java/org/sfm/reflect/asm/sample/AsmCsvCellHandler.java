package org.sfm.reflect.asm.sample;


import org.sfm.beans.DbObject;
import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.impl.*;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.Instantiator;

public class AsmCsvCellHandler extends CsvCellHandler<DbObject> {

    /**
     * mapping information
     */
    protected final DelayedCellSetter<DbObject, ?> delayedCellSetter0;
    protected final DelayedCellSetter<DbObject, ?> delayedCellSetter1;
    protected final DelayedCellSetter<DbObject, ?> delayedCellSetter2;
    protected final CellSetter<DbObject> setter3;
    protected final CellSetter<DbObject> setter4;
    protected final CellSetter<DbObject> setter5;


    public AsmCsvCellHandler(Instantiator<CsvCellHandler<DbObject>, DbObject> instantiator, DelayedCellSetter<DbObject, ?>[] delayedCellSetters, CellSetter<DbObject>[] setters, CsvColumnKey[] columns, ParsingContext parsingContext, FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler) {
        super(instantiator, columns, delayedCellSetters.length, setters.length, parsingContext, fieldErrorHandler);
        delayedCellSetter0 = delayedCellSetters[0];
        delayedCellSetter1 = delayedCellSetters[1];
        delayedCellSetter2 = delayedCellSetters[2];

        setter3 = setters[0];
        setter4 = setters[1];
        setter5 = setters[2];
    }


    @Override
    public void applyDelayedSetters() {
        try {
            delayedCellSetter0.set(currentInstance);
        } catch(Exception e) {
            fieldErrorHandler.errorMappingField(getColumn(0), this, currentInstance, e);
        }
        try {
            delayedCellSetter1.set(currentInstance);
        } catch(Exception e) {
            fieldErrorHandler.errorMappingField(getColumn(1), this, currentInstance, e);
        }
        try {
            delayedCellSetter2.set(currentInstance);
        } catch(Exception e) {
            fieldErrorHandler.errorMappingField(getColumn(2), this, currentInstance, e);
        }
    }

    @Override
    public DelayedCellSetter<DbObject, ?> getDelayedCellSetter(int index) {
        switch(index) {
            case 0:
                return delayedCellSetter0;
            case 1:
                return delayedCellSetter1;
            case 2:
                return delayedCellSetter2;
        }
        return null;
    }

    @Override
    public CellSetter<DbObject> getCellSetter(int index) {
        switch(index) {
            case 3:
                return setter3;
            case 4:
                return setter4;
            case 5:
                return setter5;
        }
        return null;

    }
}

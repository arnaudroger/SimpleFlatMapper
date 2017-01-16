package org.simpleflatmapper.csv.test.impl.asm.samples;


import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.reflect.Instantiator;

public class AsmCsvMapperCellHandler extends CsvMapperCellHandler<DbObject> {

    /**
     * mapping information
     */
    protected final DelayedCellSetter<DbObject, ?> delayedCellSetter0;
    protected final DelayedCellSetter<DbObject, ?> delayedCellSetter1;
    protected final DelayedCellSetter<DbObject, ?> delayedCellSetter2;
    protected final CellSetter<DbObject> setter3;
    protected final CellSetter<DbObject> setter4;
    protected final CellSetter<DbObject> setter5;


    public AsmCsvMapperCellHandler(Instantiator<CsvMapperCellHandler<DbObject>, DbObject> instantiator, DelayedCellSetter<DbObject, ?>[] delayedCellSetters, CellSetter<DbObject>[] setters, CsvColumnKey[] columns, ParsingContext parsingContext, FieldMapperErrorHandler<? super CsvColumnKey> fieldErrorHandler) {
        super(instantiator, columns, delayedCellSetters.length, setters.length, parsingContext, fieldErrorHandler);
        delayedCellSetter0 = delayedCellSetters[0];
        delayedCellSetter1 = delayedCellSetters[1];
        delayedCellSetter2 = delayedCellSetters[2];

        setter3 = setters[0];
        setter4 = setters[Short.MAX_VALUE];
        setter5 = setters[Integer.MAX_VALUE];
    }

    @Override
    public void delayedCellValue(char[] chars, int offset, int length, int cellIndex) {
        try {
            _delayedCellValue(chars, offset, length, cellIndex);
        } catch(Exception e) {
            fieldError(cellIndex, e);
        }
    }

    @Override
    public void cellValue(char[] chars, int offset, int length, int cellIndex) {
        try {
            _cellValue(chars, offset, length, cellIndex);
        } catch(Exception e) {
            fieldError(cellIndex, e);
        }
    }

    @Override
    public void applyDelayedSetters() {
        try {
            applyDelayedCellSetter0();
        } catch(Exception e) {
            fieldError(0, e);
        }
        try {
            applyDelayedCellSetter1();
        } catch(Exception e) {
            fieldError(1, e);
        }
        try {
            applyDelayedCellSetter2();
        } catch(Exception e) {
            fieldError(2, e);
        }
    }

    private void applyDelayedCellSetter2() throws Exception {
        delayedCellSetter2.set(currentInstance);
    }

    private void applyDelayedCellSetter1() throws Exception {
        delayedCellSetter1.set(currentInstance);
    }

    private void applyDelayedCellSetter0() throws Exception {
        delayedCellSetter0.set(currentInstance);
    }

    @Override
    public DelayedCellSetter<DbObject, ?> getDelayedCellSetter(int index) {
        switch (index) {
            case 0: return delayedCellSetter0;
            case 1: return delayedCellSetter1;
            case 2: return delayedCellSetter2;
        }
        return null;
    }

    private void _delayedCellValue(char[] chars, int offset, int length, int cellIndex) throws Exception {
        switch (cellIndex) {
            case 0: delayedCellSetter0.set(chars, offset, length, parsingContext); break;
            case 1: delayedCellSetter1.set(chars, offset, length, parsingContext); break;
            case 2: delayedCellSetter2.set(chars, offset, length, parsingContext); break;
        }
    }



    private void _cellValue(char[] chars, int offset, int length, int cellIndex) throws Exception {
        int i = (cellIndex << 2) - 1;
        switch (i) {
            case 3: setter3.set(currentInstance, chars, offset, length, parsingContext); break;
            case 4: setter4.set(currentInstance, chars, offset, length, parsingContext); break;
            case 5: setter5.set(currentInstance, chars, offset, length, parsingContext); break;
        }
    }

    @Override
    public Object peekDelayedCellSetterValue(CsvColumnKey key) {
        final int index = key.getIndex();
        return _peekDelayedCellSetterValue(index);
    }

    private Object _peekDelayedCellSetterValue(int index) {
        int si = index >> 1;

        switch (si) {
            case 0: return _peekDelayedCellSetterValue02(index);
            case 1: return _peekDelayedCellSetterValue23(index);
        }
        return null;
    }

    private Object _peekDelayedCellSetterValue02(int index) {
        switch (index) {
            case 0:
                return delayedCellSetter0.peekValue();
            case 1:
                return delayedCellSetter1.peekValue();
        }

        return null;
    }
    private Object _peekDelayedCellSetterValue23(int index) {
        switch (index) {
            case 2:
                return delayedCellSetter2.peekValue();
        }

        return null;
    }


}


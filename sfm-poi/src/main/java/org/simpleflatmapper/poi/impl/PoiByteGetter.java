package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;

public class PoiByteGetter implements Getter<Row, Byte>, ByteGetter<Row> {

    private final int index;

    public PoiByteGetter(int index) {
        this.index = index;
    }

    @Override
    public Byte get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (byte)cell.getNumericCellValue();
        } else {
            return null;
        }
    }

    @Override
    public byte getByte(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (byte)cell.getNumericCellValue();
        } else {
            return 0;
        }
    }
}

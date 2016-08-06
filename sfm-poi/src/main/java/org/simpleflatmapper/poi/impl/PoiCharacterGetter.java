package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;

public class PoiCharacterGetter implements Getter<Row, Character>, CharacterGetter<Row> {

    private final int index;

    public PoiCharacterGetter(int index) {
        this.index = index;
    }

    @Override
    public Character get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (char)cell.getNumericCellValue();
        } else {
            return null;
        }
    }

    @Override
    public char getCharacter(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (char)cell.getNumericCellValue();
        } else {
            return 0;
        }
    }
}

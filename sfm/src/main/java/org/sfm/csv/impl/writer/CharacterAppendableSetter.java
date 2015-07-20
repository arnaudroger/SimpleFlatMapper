package org.sfm.csv.impl.writer;

import org.sfm.csv.CellWriter;
import org.sfm.reflect.primitive.CharacterSetter;

public class CharacterAppendableSetter implements CharacterSetter<Appendable> {

    private final CellWriter cellWriter;

    public CharacterAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setCharacter(Appendable target, char value) throws Exception {
        cellWriter.writeValue(Integer.toString((int)value), target);
    }
}

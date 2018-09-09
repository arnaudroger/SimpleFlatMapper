package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.CharacterContextualSetter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;

public class CharacterAppendableSetter implements CharacterContextualSetter<Appendable> {

    private final CellWriter cellWriter;

    public CharacterAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setCharacter(Appendable target, char value, Context context) throws Exception {
        cellWriter.writeValue(Integer.toString((int)value), target);
    }
}

package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.CharacterContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

public class CsvCharGetter implements ContextualGetter<CsvRow, Character>, CharacterContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvCharGetter(int index) {
        this.index = index;
    }

    @Override
    public Character get(CsvRow target, Context context)  {
        return get(target, context, index);
    }

    public static Character get(CsvRow target, Context context, int index) {
        return target.getChar(index);
    }

    @Override
    public char getCharacter(CsvRow target, Context context)  {
        return getCharacter(target, context, index);
    }

    public static char getCharacter(CsvRow target, Context context, int index) {
        return target.getChar(index);
    }
}

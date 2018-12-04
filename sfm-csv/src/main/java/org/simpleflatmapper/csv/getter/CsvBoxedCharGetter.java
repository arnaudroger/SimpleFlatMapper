package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class CsvBoxedCharGetter implements ContextualGetter<CsvRow, Character> {
    public final int index;

    public CsvBoxedCharGetter(int index) {
        this.index = index;
    }

    @Override
    public Character get(CsvRow target, Context context)  {
        return target.getBoxedChar(index);
    }

}

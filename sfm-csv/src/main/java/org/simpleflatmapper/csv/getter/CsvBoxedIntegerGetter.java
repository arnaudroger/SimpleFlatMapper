package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class CsvBoxedIntegerGetter implements ContextualGetter<CsvRow, Integer> {
    public final int index;

    public CsvBoxedIntegerGetter(int index) {
        this.index = index;
    }

    @Override
    public Integer get(CsvRow target, Context context)  {
        return target.getBoxedInt(index);
    }
}

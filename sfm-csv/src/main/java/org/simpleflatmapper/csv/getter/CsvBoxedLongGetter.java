package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class CsvBoxedLongGetter implements ContextualGetter<CsvRow, Long> {
    public final int index;

    public CsvBoxedLongGetter(int index) {
        this.index = index;
    }

    @Override
    public Long get(CsvRow target, Context context)  {
        return target.getBoxedLong(index);
    }
}

package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class CsvBoxedShortGetter implements ContextualGetter<CsvRow, Short> {
    public final int index;

    public CsvBoxedShortGetter(int index) {
        this.index = index;
    }

    @Override
    public Short get(CsvRow target, Context context)  {
        return target.getBoxedShort(index);
    }

}

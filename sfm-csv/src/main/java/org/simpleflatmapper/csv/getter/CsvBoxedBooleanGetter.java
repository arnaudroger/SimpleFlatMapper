package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class CsvBoxedBooleanGetter implements ContextualGetter<CsvRow, Boolean> {
    public final int index;

    public CsvBoxedBooleanGetter(int index) {
        this.index = index;
    }

    @Override
    public Boolean get(CsvRow target, Context context)  {
        return target.getBoxedBoolean(index);
    }

}

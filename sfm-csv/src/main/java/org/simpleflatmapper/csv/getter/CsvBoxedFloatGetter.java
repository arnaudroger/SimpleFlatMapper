package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class CsvBoxedFloatGetter implements ContextualGetter<CsvRow, Float> {
    public final int index;

    public CsvBoxedFloatGetter(int index) {
        this.index = index;
    }

    @Override
    public Float get(CsvRow target, Context context)  {
        return target.getBoxedFloat(index);
    }
}

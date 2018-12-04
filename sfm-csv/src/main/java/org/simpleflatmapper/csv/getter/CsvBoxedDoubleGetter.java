package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class CsvBoxedDoubleGetter implements ContextualGetter<CsvRow, Double> {
    public final int index;

    public CsvBoxedDoubleGetter(int index) {
        this.index = index;
    }

    @Override
    public Double get(CsvRow target, Context context)  {
        return target.getBoxedDouble(index);
    }
}

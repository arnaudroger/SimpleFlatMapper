package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.DoubleContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

public class CsvDoubleGetter implements ContextualGetter<CsvRow, Double>, DoubleContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvDoubleGetter(int index) {
        this.index = index;
    }

    @Override
    public Double get(CsvRow target, Context context)  {
        return get(target, context, index);
    }

    public static double get(CsvRow target, Context context, int index) {
        return target.getDouble(index);
    }

    @Override
    public double getDouble(CsvRow target, Context context)  {
        return getDouble(target, context, index);
    }

    public static double getDouble(CsvRow target, Context context, int index) {
        return target.getDouble(index);
    }
}

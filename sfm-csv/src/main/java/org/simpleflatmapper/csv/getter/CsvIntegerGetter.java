package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.IntContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

public class CsvIntegerGetter implements ContextualGetter<CsvRow, Integer>, IntContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvIntegerGetter(int index) {
        this.index = index;
    }

    @Override
    public Integer get(CsvRow target, Context context)  {
        return get(target, context, index);
    }

    public static Integer get(CsvRow target, Context context, int index) {
        return target.getInt(index);
    }

    @Override
    public int getInt(CsvRow target, Context context)  {
        return getInt(target, context, index);
    }

    public static int getInt(CsvRow target, Context context, int index) {
        return target.getInt(index);
    }
}

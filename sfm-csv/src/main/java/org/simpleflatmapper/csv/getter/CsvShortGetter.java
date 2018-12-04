package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;
import org.simpleflatmapper.map.getter.ShortContextualGetter;

public class CsvShortGetter implements ContextualGetter<CsvRow, Short>, ShortContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvShortGetter(int index) {
        this.index = index;
    }

    @Override
    public Short get(CsvRow target, Context context)  {
        return get(target, context, index);
    }

    public static Short get(CsvRow target, Context context, int index) {
        return target.getShort(index);
    }

    @Override
    public short getShort(CsvRow target, Context context)  {
        return getShort(target, context, index);
    }

    public static short getShort(CsvRow target, Context context, int index) {
        return target.getShort(index);
    }
}

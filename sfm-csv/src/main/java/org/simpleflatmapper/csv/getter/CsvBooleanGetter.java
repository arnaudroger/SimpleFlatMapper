package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.BooleanContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

public class CsvBooleanGetter implements ContextualGetter<CsvRow, Boolean>, BooleanContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvBooleanGetter(int index) {
        this.index = index;
    }

    @Override
    public Boolean get(CsvRow target, Context context)  {
        return get(target, context, index);
    }

    public static boolean get(CsvRow target, Context context, int index) {
        return target.getBoolean(index);
    }

    @Override
    public boolean getBoolean(CsvRow target, Context context)  {
        return getBoolean(target, context, index);
    }

    public static boolean getBoolean(CsvRow target, Context context, int index) {
        return target.getBoolean(index);
    }
}

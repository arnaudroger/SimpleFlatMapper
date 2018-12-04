package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

public class CsvStringGetter implements ContextualGetter<CsvRow, String>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvStringGetter(int index) {
        this.index = index;
    }

    @Override
    public String get(CsvRow target, Context context)  {
        return get(target, context, index);
    }

    public static String get(CsvRow target, Context context, int index) {
        return target.getString(index);
    }
}

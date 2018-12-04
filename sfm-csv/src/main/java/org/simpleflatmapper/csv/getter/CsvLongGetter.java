package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.csv.mapper.CsvRowGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.LongContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

public class CsvLongGetter implements ContextualGetter<CsvRow, Long>, LongContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvLongGetter(int index) {
        this.index = index;
    }

    @Override
    public Long get(CsvRow target, Context context)  {
        return CsvLongGetter.this.get(target, context, index);
    }

    public static Long get(CsvRow target, Context context, int index) {
        return target.getLong(index);
    }

    @Override
    public long getLong(CsvRow target, Context context)  {
        return getLong(target, context, index);
    }

    public static long getLong(CsvRow target, Context context, int index) {
        return target.getLong(index);
    }
}

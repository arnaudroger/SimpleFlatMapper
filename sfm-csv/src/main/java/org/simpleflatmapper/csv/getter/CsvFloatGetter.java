package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.csv.mapper.CsvRowGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.FloatContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

public class CsvFloatGetter implements ContextualGetter<CsvRow, Float>, FloatContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvFloatGetter(int index) {
        this.index = index;
    }

    @Override
    public Float get(CsvRow target, Context context)  {
        return CsvFloatGetter.this.get(target, context, index);
    }

    public static Float get(CsvRow target, Context context, int index) {
        return target.getFloat(index);
    }

    @Override
    public float getFloat(CsvRow target, Context context)  {
        return getFloat(target, context, index);
    }

    public static float getFloat(CsvRow target, Context context, int index) {
        return target.getFloat(index);
    }
}

package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

import java.math.BigDecimal;

public class CsvBigDecimalGetter implements ContextualGetter<CsvRow, BigDecimal>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvBigDecimalGetter(int index) {
        this.index = index;
    }

    @Override
    public BigDecimal get(CsvRow target, Context context)  {
        return get(target, context, index);
    }

    public static BigDecimal get(CsvRow target, Context context, int index) {
        return target.getBigDecimal(index);
    }
}

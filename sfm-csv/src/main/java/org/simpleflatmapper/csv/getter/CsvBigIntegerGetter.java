package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

import java.math.BigInteger;

public class CsvBigIntegerGetter implements ContextualGetter<CsvRow, BigInteger>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvBigIntegerGetter(int index) {
        this.index = index;
    }

    @Override
    public BigInteger get(CsvRow target, Context context)  {
        return get(target, context, index);
    }

    public static BigInteger get(CsvRow target, Context context, int index) {
        return target.getBigInteger(index);
    }
}

package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ByteContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

public class CsvByteGetter implements ContextualGetter<CsvRow, Byte>, ByteContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvByteGetter(int index) {
        this.index = index;
    }

    @Override
    public Byte get(CsvRow target, Context context)  {
        return get(target, context, index);
    }

    public static Byte get(CsvRow target, Context context, int index) {
        return target.getByte(index);
    }

    @Override
    public byte getByte(CsvRow target, Context context)  {
        return getByte(target, context, index);
    }

    public static byte getByte(CsvRow target, Context context, int index) {
        return target.getByte(index);
    }
}

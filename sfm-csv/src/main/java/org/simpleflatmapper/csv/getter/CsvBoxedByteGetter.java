package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class CsvBoxedByteGetter implements ContextualGetter<CsvRow, Byte> {
    public final int index;

    public CsvBoxedByteGetter(int index) {
        this.index = index;
    }

    @Override
    public Byte get(CsvRow target, Context context)  {
        return target.getBoxedByte(index);
    }
}

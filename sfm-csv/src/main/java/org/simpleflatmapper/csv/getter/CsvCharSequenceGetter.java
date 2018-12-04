package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.OptimizableIndexedContextualGetter;

public class CsvCharSequenceGetter implements ContextualGetter<CsvRow, CharSequence>, OptimizableIndexedContextualGetter {
    public final int index;

    public CsvCharSequenceGetter(int index) {
        this.index = index;
    }

    @Override
    public CharSequence get(CsvRow target, Context context)  {
        return get(target, context, index);
    }
    
    public static CharSequence get(CsvRow target, Context context, int index) {
        return target.getCharSequence(index);
    }
}

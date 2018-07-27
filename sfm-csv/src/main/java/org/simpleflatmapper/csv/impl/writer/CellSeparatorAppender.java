package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;

public class CellSeparatorAppender<T>
        implements FieldMapper<T, Appendable> {

    private final CellWriter cellWriter;

    public CellSeparatorAppender(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void mapTo(T source, Appendable target, MappingContext<? super T> context) throws Exception {
        cellWriter.nextCell(target);
    }
}

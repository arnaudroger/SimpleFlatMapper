package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.csv.CellWriter;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;

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

package org.sfm.csv.impl.writer;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.*;

public class CellSeparatorAppender<T>
        implements FieldMapper<T, Appendable> {

    private final CsvCellWriter cellWriter;

    public CellSeparatorAppender(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void mapTo(T source, Appendable target, MappingContext<T> context) throws Exception {
        cellWriter.nextCell(target);
    }
}

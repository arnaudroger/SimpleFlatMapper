package org.sfm.csv.impl.writer;

import org.sfm.csv.CellWriter;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;

public class EndOfRowAppender<S>
        implements FieldMapper<S, Appendable> {

    private final CellWriter cellWriter;

    public EndOfRowAppender(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void mapTo(S source, Appendable target, MappingContext<? super S> context) throws Exception {
        cellWriter.endOfRow(target);
    }
}

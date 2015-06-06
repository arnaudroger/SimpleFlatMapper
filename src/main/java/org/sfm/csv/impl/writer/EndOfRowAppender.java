package org.sfm.csv.impl.writer;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;

public class EndOfRowAppender<S>
        implements FieldMapper<S, Appendable> {

    private final CsvCellWriter cellWriter;

    public EndOfRowAppender(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void mapTo(S source, Appendable target, MappingContext<S> context) throws Exception {
        cellWriter.endOfRow(target);
    }
}

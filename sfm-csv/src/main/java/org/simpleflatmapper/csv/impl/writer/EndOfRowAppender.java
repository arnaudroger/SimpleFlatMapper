package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;

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

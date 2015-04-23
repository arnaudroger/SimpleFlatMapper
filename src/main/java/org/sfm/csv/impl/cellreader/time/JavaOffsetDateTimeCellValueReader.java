package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class JavaOffsetDateTimeCellValueReader implements CellValueReader<OffsetDateTime> {

    private final DateTimeFormatter formatter;

    public JavaOffsetDateTimeCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public OffsetDateTime read(CharSequence value, ParsingContext parsingContext) {
        return OffsetDateTime.parse(value, formatter);
    }

}

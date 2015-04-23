package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

public class JavaOffsetTimeCellValueReader implements CellValueReader<OffsetTime> {

    private final DateTimeFormatter formatter;

    public JavaOffsetTimeCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public OffsetTime read(CharSequence value, ParsingContext parsingContext) {
        return OffsetTime.parse(value, formatter);
    }

}

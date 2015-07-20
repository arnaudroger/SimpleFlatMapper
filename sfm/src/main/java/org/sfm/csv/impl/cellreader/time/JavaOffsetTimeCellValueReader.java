package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

public class JavaOffsetTimeCellValueReader implements CellValueReader<OffsetTime> {

    private final DateTimeFormatter formatter;

    public JavaOffsetTimeCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public OffsetTime read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return OffsetTime.parse(new String(chars, offset, length), formatter);
    }

}

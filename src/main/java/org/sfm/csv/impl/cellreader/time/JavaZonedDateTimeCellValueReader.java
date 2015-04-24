package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class JavaZonedDateTimeCellValueReader implements CellValueReader<ZonedDateTime> {

    private final DateTimeFormatter formatter;

    public JavaZonedDateTimeCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public ZonedDateTime read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return ZonedDateTime.parse(new String(chars, offset, length), formatter);
    }

}

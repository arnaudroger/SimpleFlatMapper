package org.simpleflatmapper.csv.impl.cellreader.time;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

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

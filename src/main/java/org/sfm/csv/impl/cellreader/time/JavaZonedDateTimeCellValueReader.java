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
    public ZonedDateTime read(CharSequence value, ParsingContext parsingContext) {
        return ZonedDateTime.parse(value, formatter);
    }

}

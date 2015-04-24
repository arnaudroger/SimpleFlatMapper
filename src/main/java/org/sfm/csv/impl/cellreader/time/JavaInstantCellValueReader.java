package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class JavaInstantCellValueReader implements CellValueReader<Instant> {

    private final DateTimeFormatter formatter;

    public JavaInstantCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public Instant read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return ZonedDateTime.parse(new String(chars, offset, length), formatter).toInstant();
    }

}

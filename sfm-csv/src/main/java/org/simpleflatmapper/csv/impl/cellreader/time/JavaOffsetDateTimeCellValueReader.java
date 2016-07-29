package org.simpleflatmapper.csv.impl.cellreader.time;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class JavaOffsetDateTimeCellValueReader implements CellValueReader<OffsetDateTime> {

    private final DateTimeFormatter formatter;

    public JavaOffsetDateTimeCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public OffsetDateTime read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return OffsetDateTime.parse(new String(chars, offset, length), formatter);
    }

}

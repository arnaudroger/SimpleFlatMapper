package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JavaLocalDateTimeCellValueReader implements CellValueReader<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public JavaLocalDateTimeCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LocalDateTime read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return LocalDateTime.parse(new String(chars, offset, length), formatter);
    }

}

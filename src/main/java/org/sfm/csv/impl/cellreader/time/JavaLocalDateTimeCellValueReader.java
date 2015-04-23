package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JavaLocalDateTimeCellValueReader implements CellValueReader<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public JavaLocalDateTimeCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LocalDateTime read(CharSequence value, ParsingContext parsingContext) {
        return LocalDateTime.parse(value, formatter);
    }

}

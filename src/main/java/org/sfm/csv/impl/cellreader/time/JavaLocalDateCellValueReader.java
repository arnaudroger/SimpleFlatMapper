package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JavaLocalDateCellValueReader implements CellValueReader<LocalDate> {

    private final DateTimeFormatter formatter;

    public JavaLocalDateCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LocalDate read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return LocalDate.parse(new String(chars, offset, length), formatter);
    }

}

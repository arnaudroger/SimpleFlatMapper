package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

import java.time.Year;
import java.time.format.DateTimeFormatter;

public class JavaYearCellValueReader implements CellValueReader<Year> {

    private final DateTimeFormatter formatter;

    public JavaYearCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public Year read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return Year.parse(new String(chars, offset, length), formatter);
    }

}

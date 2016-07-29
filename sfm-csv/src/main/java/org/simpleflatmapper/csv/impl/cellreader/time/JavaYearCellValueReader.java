package org.simpleflatmapper.csv.impl.cellreader.time;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

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

package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class JavaYearMonthCellValueReader implements CellValueReader<YearMonth> {

    private final DateTimeFormatter formatter;

    public JavaYearMonthCellValueReader(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public YearMonth read(CharSequence value, ParsingContext parsingContext) {
        return YearMonth.parse(value, formatter);
    }

}

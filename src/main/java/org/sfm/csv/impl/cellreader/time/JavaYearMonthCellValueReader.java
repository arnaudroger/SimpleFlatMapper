package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class JavaYearMonthCellValueReader implements CellValueReader<YearMonth> {

    private final DateTimeFormatter formatter;

    public JavaYearMonthCellValueReader(String format, TimeZone timeZone) {
        formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.of(timeZone.getID()));
    }

    @Override
    public YearMonth read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return YearMonth.parse(new String(chars, offset, length), formatter);
    }

}

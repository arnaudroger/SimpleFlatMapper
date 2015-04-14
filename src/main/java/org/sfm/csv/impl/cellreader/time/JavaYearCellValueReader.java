package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class JavaYearCellValueReader implements CellValueReader<Year> {

    private final DateTimeFormatter formatter;

    public JavaYearCellValueReader(String format, TimeZone timeZone) {
        formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.of(timeZone.getID()));
    }

    @Override
    public Year read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return Year.parse(new String(chars, offset, length), formatter);
    }

}

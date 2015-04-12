package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class JavaLocalDateCellValueReader implements CellValueReader<LocalDate> {

    private final DateTimeFormatter formatter;

    public JavaLocalDateCellValueReader(String format, TimeZone timeZone) {
        formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.of(timeZone.getID()));
    }

    @Override
    public LocalDate read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return LocalDate.parse(new String(chars, offset, length), formatter);
    }

}

package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class JavaZonedDateTimeCellValueReader implements CellValueReader<ZonedDateTime> {

    private final DateTimeFormatter formatter;

    public JavaZonedDateTimeCellValueReader(String format, TimeZone timeZone) {
        formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.of(timeZone.getID()));
    }

    @Override
    public ZonedDateTime read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return ZonedDateTime.parse(new String(chars, offset, length), formatter);
    }

}

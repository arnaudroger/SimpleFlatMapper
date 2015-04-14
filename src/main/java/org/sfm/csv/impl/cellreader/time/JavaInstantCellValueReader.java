package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class JavaInstantCellValueReader implements CellValueReader<Instant> {

    private final DateTimeFormatter formatter;

    public JavaInstantCellValueReader(String format, TimeZone timeZone) {
        formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.of(timeZone.getID()));
    }

    @Override
    public Instant read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return ZonedDateTime.parse(new String(chars, offset, length), formatter).toInstant();
    }

}

package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class JavaOffsetDateTimeCellValueReader implements CellValueReader<OffsetDateTime> {

    private final DateTimeFormatter formatter;

    public JavaOffsetDateTimeCellValueReader(String format, TimeZone timeZone) {
        formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.of(timeZone.getID()));
    }

    @Override
    public OffsetDateTime read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return OffsetDateTime.parse(new String(chars, offset, length), formatter);
    }

}

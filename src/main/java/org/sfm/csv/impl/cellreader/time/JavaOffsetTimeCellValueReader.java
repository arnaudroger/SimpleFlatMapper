package org.sfm.csv.impl.cellreader.time;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class JavaOffsetTimeCellValueReader implements CellValueReader<OffsetTime> {

    private final DateTimeFormatter formatter;

    public JavaOffsetTimeCellValueReader(String format, TimeZone timeZone) {
        formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.of(timeZone.getID()));
    }

    @Override
    public OffsetTime read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return OffsetTime.parse(new String(chars, offset, length), formatter);
    }

}

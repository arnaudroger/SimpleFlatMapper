package org.sfm.csv.impl.cellreader.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.StringCellValueReader;

import java.util.TimeZone;

public class JodaLocalDateCellValueReader implements CellValueReader<LocalDate> {
    private final DateTimeFormatter fmt;

    public JodaLocalDateCellValueReader(String dateFormat, TimeZone timeZone) {
        this.fmt = DateTimeFormat.forPattern(dateFormat).withZone(DateTimeZone.forTimeZone(timeZone));
    }

    @Override
    public LocalDate read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        if (length == 0) return null;
        return fmt .parseLocalDate(StringCellValueReader.readString(chars, offset, length));
    }
}

package org.sfm.csv.impl.cellreader.joda;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.StringCellValueReader;

public class JodaDateTimeCellValueReader implements CellValueReader<DateTime> {
    private final DateTimeFormatter fmt;

    public JodaDateTimeCellValueReader(String dateFormat) {
        this.fmt = DateTimeFormat.forPattern(dateFormat);
    }

    @Override
    public DateTime read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        if (length == 0) return null;
        return fmt .parseDateTime(StringCellValueReader.readString(chars, offset, length));
    }
}

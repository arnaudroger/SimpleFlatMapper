package org.sfm.csv.impl.cellreader.joda;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.StringCellValueReader;

public class JodaDateTimeCellValueReader implements CellValueReader<DateTime> {
    private final DateTimeFormatter fmt;

    public JodaDateTimeCellValueReader(DateTimeFormatter fmt) {
        this.fmt = fmt;
    }

    public DateTime read(CharSequence value, ParsingContext parsingContext) {
        if (value.length() == 0) return null;
        return fmt .parseDateTime(StringCellValueReader.readString(value));
    }
}

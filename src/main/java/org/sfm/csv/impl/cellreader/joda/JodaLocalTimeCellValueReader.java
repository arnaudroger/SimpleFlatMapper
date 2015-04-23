package org.sfm.csv.impl.cellreader.joda;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.StringCellValueReader;

public class JodaLocalTimeCellValueReader implements CellValueReader<LocalTime> {
    private final DateTimeFormatter fmt;

    public JodaLocalTimeCellValueReader(DateTimeFormatter fmt) {
        this.fmt = fmt;
    }

    @Override
    public LocalTime read(CharSequence value, ParsingContext parsingContext) {
        if (value.length() == 0) return null;
        return fmt .parseLocalTime(StringCellValueReader.readString(value));
    }
}

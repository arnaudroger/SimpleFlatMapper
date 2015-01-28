package org.sfm.csv.impl.cellreader.joda;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.StringCellValueReader;

public class JodaLocalDateTimeCellValueReader implements CellValueReader<LocalDateTime> {
    private final DateTimeFormatter fmt;

    public JodaLocalDateTimeCellValueReader(String dateFormat) {
        this.fmt = DateTimeFormat.forPattern(dateFormat);
    }

    @Override
    public LocalDateTime read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        if (length == 0) return null;
        return fmt .parseLocalDateTime(StringCellValueReader.readString(chars, offset, length));
    }
}

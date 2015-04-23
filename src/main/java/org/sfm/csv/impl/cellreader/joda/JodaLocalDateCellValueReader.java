package org.sfm.csv.impl.cellreader.joda;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.StringCellValueReader;

public class JodaLocalDateCellValueReader implements CellValueReader<LocalDate> {
    private final DateTimeFormatter fmt;

    public JodaLocalDateCellValueReader(DateTimeFormatter fmt) {
        this.fmt = fmt;
    }

    @Override
    public LocalDate read(CharSequence value, ParsingContext parsingContext) {
        if (value.length() == 0) return null;
        return fmt .parseLocalDate(StringCellValueReader.readString(value));
    }
}

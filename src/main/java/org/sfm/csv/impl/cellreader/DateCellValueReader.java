package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.ParsingException;
import org.sfm.map.ParsingContextProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateCellValueReader implements CellValueReader<Date>, ParsingContextProvider {
	
	private final int index;
    private final TimeZone timeZone;
    private final String pattern;
	public DateCellValueReader(int index, String pattern, TimeZone timeZone) {
        if (timeZone == null) throw new NullPointerException();
        if (pattern == null) throw new NullPointerException();
        this.index = index;
        this.timeZone = timeZone;
        this.pattern = pattern;
	}
	
	@Override
	public Date read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;

		String str = StringCellValueReader.readString(chars, offset, length);
		try {
			DateFormat df = (DateFormat) parsingContext.getContext(index);
			return df.parse(str);
		} catch (ParseException e) {
			throw new ParsingException(e.getMessage());
		}
	}

    @Override
    public Object newContext() {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(timeZone);
        return sdf;
    }

    @Override
    public String toString() {
        return "DateCellValueReader{" +
                "index=" + index +
                ", timeZone=" + timeZone.getDisplayName() +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}

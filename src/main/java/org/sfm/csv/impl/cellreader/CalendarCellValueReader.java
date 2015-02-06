package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.map.ParsingContextProvider;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CalendarCellValueReader implements CellValueReader<Calendar>, ParsingContextProvider {

	private final DateCellValueReader reader;

	public CalendarCellValueReader(int index, String pattern, TimeZone timeZone) {
		this.reader = new DateCellValueReader(index, pattern, timeZone);
	}
	
	@Override
	public Calendar read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		Date date = reader.read(chars, offset, length, parsingContext);
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		}
		return null;
	}

    @Override
    public Object newContext() {
        return reader.newContext();
    }
}

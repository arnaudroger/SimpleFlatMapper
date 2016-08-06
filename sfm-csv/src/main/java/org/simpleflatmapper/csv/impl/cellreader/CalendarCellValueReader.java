package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.map.ParsingContextProvider;

import java.util.Calendar;
import java.util.Date;

public class CalendarCellValueReader implements CellValueReader<Calendar>, ParsingContextProvider {

	private final CellValueReader<Date> reader;

	public CalendarCellValueReader(CellValueReader<Date> reader) {
		this.reader = reader;
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
        return ((ParsingContextProvider)reader).newContext();
    }

    @Override
    public String toString() {
        return "CalendarCellValueReader{" +
                "reader=" + reader +
                '}';
    }
}

package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.ParsingException;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class CalendarCellValueReader implements CellValueReader<Calendar> {

	private final DateCellValueReader reader;

	public CalendarCellValueReader(int index) {
		this.reader = new DateCellValueReader(index);
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
}

package org.sfm.csv.cell;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

public class DateCellValueReader implements CellValueReader<Date> {
	
	private int index;

	public DateCellValueReader(int index) {
		this.index = index;
	}
	
	@Override
	public Date read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		String str = StringCellValueReader.readString(chars, offset, length);
		try {
			DateFormat df = parsingContext.getDateFormat(index);
			return df.parse(str);
		} catch (ParseException e) {
			throw new ParsingException(e.getMessage());
		}
	}
}

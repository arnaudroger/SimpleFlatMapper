package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.ParsingException;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

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

package org.sfm.csv.cell;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.sfm.csv.CellValueReader;

public class DateCellValueReader implements CellValueReader<Date> {
	
	private final StringCellValueReader reader = new StringCellValueReader();
	@Override
	public Date read(byte[] bytes, int offset, int length) {
		String str = reader.read(bytes, offset, length);
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
		} catch (ParseException e) {
			throw new ParsingException(e.getMessage());
		}
	}
	@Override
	public Date read(char[] chars, int offset, int length) {
		String str = StringCellValueReader.readString(chars, offset, length);
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
		} catch (ParseException e) {
			throw new ParsingException(e.getMessage());
		}
	}
}

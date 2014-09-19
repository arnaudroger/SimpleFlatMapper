package org.sfm.csv.cell;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.sfm.csv.CellValueReader;

public class DateCellValueReader implements CellValueReader<Date> {
	private final StringCellValueReader stringReader = new StringCellValueReader();
	@Override
	public Date read(byte[] bytes, int offset, int length) {
		String str = stringReader.read(bytes, offset, length);
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
		} catch (ParseException e) {
			throw new ParsingException(e.getMessage());
		}
	}

}

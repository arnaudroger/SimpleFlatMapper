package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.map.ParsingContextProvider;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.ParsingException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class DateMultiFormatCellValueReader implements CellValueReader<Date>, ParsingContextProvider {

	private final int index;
    private final DateFormat[] sdf;
	public DateMultiFormatCellValueReader(int index, String[] pattern, TimeZone timeZone) {
        this.index = index;
        this.sdf =  new SimpleDateFormat[pattern.length];
		for(int i = 0; i < sdf.length; i++) {
			sdf[i] = new SimpleDateFormat(pattern[i]);
			sdf[i].setTimeZone(requireNonNull("timeZone", timeZone));
		}
	}
	
	@Override
	public Date read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;

		String str = new String(chars, offset, length);
		DateFormat[] dfs = (DateFormat[]) parsingContext.getContext(index);
		for(int i = dfs.length - 1; i >= 0; i--) {
			try {
				return dfs[i].parse(str);
			} catch (ParseException e) {
				// ignore
			}
		}
		throw new ParsingException("Unable to parse date '" + str + "'");
	}

    @Override
    public Object newContext() {
		DateFormat[] formats = new DateFormat[sdf.length];
		for(int i = 0; i < sdf.length; i++) {
			formats[i] = (DateFormat) sdf[i].clone();
		}
		return formats;
    }

	@Override
	public String toString() {
		return "DateMultiFormatCellValueReader{" +
				"index=" + index +
				", sdf=" + Arrays.toString(sdf) +
				'}';
	}
}

package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;
import org.sfm.csv.impl.ParsingException;
import org.sfm.map.ParsingContextProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.sfm.utils.Asserts.requireNonNull;

public class DateCellValueReader implements CellValueReader<Date>, ParsingContextProvider {
	
	private final int index;
    private final SimpleDateFormat sdf;
	public DateCellValueReader(int index, String pattern, TimeZone timeZone) {
        this.index = index;
        this.sdf =  new SimpleDateFormat(pattern);
        this.sdf.setTimeZone(requireNonNull("timeZone", timeZone));
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
        return sdf.clone();
    }

    @Override
    public String toString() {
        return "DateCellValueReader{" +
                "index=" + index +
                ", timeZone=" + sdf.getTimeZone().getDisplayName() +
                ", pattern='" + sdf.toPattern() + '\'' +
                '}';
    }
}

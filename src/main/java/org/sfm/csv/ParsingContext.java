package org.sfm.csv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ParsingContext {

	private final DateFormat[] dateFormats;
	
	public ParsingContext(String[] formats) {
		dateFormats = new DateFormat[formats.length];
		for(int i = 0 ; i < dateFormats.length; i++ ) {
			if (formats[i] != null) {
				dateFormats[i] = new SimpleDateFormat(formats[i]);
			}
		}
	}
	
	public DateFormat getDateFormat(int index) {
		return dateFormats[index];
	}
	
}

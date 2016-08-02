package org.simpleflatmapper.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static Date toDate(String str) throws ParseException {
		//df.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		return df.parse(str);
	}
}

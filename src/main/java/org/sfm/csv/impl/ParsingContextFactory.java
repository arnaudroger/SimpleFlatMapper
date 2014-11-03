package org.sfm.csv.impl;


public class ParsingContextFactory {

	private final String[] dateFormats;
	
	public ParsingContextFactory(int size) {
		dateFormats = new String[size];
	}
	
	public void setDateFormat(int index, String format) {
		dateFormats[index] = format;
	}
	
	public ParsingContext newContext() {
		return new ParsingContext(dateFormats);
	}
}

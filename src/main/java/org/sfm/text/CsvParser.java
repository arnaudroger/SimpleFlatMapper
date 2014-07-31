package org.sfm.text;

import java.io.IOException;
import java.io.Reader;

public final class CsvParser {
	
	static enum State {
		IN_QUOTE, QUOTE, NONE
	}
	private final char[] buffer = new char[1<<16];
	
	public <CH extends CellHandler> CH parser(Reader reader, CH handler) throws IOException {
		int l;
		State currentState = State.NONE;
		long currentRow = 0, currentCol = 0;
		StringBuilder sb = new StringBuilder();
		
		char c = 0;
		while((l = reader.read(buffer)) != -1) {
			for(int i = 0; i < l; i++) {
				c = buffer[i];
				
				switch(c) {
				case '"':
					if (sb.length() == 0) {
						currentState = State.IN_QUOTE;
					} else if (currentState == State.IN_QUOTE) {
						currentState = State.QUOTE;
					} else {
						sb.append('"');
						if (currentState == State.QUOTE) {
							currentState = State.IN_QUOTE;
						}
					}
					break;
				case '\r':
					sb.append('\n');
					break;
				case ',':
				case '\n':
					if (currentState != State.IN_QUOTE) {
						handler.cell(currentRow, currentCol, sb);
						sb = new StringBuilder();
						currentState = State.NONE;
					} else {
						sb.append(c);
					}
					if (c == ',') {
						currentCol ++;
					} else {
						currentCol = 0;
						currentRow ++;
					}
					break;
					default:
						sb.append(c);
				}
			}
		}
		
		if (sb.length() > 0 || c == ',' ) {
			handler.cell(currentRow, currentCol, sb);
		}
		
		return handler;
	}
}

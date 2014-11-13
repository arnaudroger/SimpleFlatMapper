package org.sfm.csv.parser;


public final class CharConsumer {

	public static final int IN_QUOTE = 4;
	public static final int IN_CR = 2;
	public static final int QUOTE = 1;
	public static final int NONE = 0;

	public static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	public static final int ALL_QUOTES = QUOTE | IN_QUOTE;

	
	private int currentState = NONE;

	private final CharsCellHandler handler;

	public CharConsumer(CharsCellHandler handler) {
		this.handler = handler;
	}
	
	public void handleChar(CharBuffer csvBuffer) {
		char c = csvBuffer.getCurrentChar();
		switch (c) {
		case ',':
			newCellIfNotInQuote(csvBuffer);
			break;
		case '\n':
			handleEndOfLineLF(csvBuffer);
			break;
		case '\r':
			handleEndOfLineCR(csvBuffer);
			return;
		case '"':
			quote(csvBuffer);
			break;
		}
		currentState = currentState & TURN_OFF_IN_CR_MASK;
	}

	private void newCellIfNotInQuote(CharBuffer csvBuffer) {
		if (isInQuote())
			return;
		
		newCell(csvBuffer);
	}

	private void handleEndOfLineLF(CharBuffer csvBuffer) {
		if (isInQuote())
			return;

		if (currentState != IN_CR) {
			endOfRow(csvBuffer);
		} else {
			csvBuffer.markConsume(csvBuffer.getConsumedIndex() + 1);
		}
	}

	private boolean isInQuote() {
		return currentState == IN_QUOTE;
	}

	private void handleEndOfLineCR(CharBuffer csvBuffer) {
		if (isInQuote())
			return;

		endOfRow(csvBuffer);
		currentState = IN_CR;
	}

	private void endOfRow(CharBuffer csvBuffer) {
		newCell(csvBuffer);
		if (!handler.endOfRow()) {
			csvBuffer.markStop();
		}
	}

	private void quote(CharBuffer csvBuffer) {
		if (csvBuffer.isAllConsumed()) {
			currentState = IN_QUOTE;
		} else {
			currentState = currentState ^ ALL_QUOTES;
		}
	}

	private void newCell(CharBuffer csvBuffer) {;
		handler.newCell(csvBuffer.getCharBuffer(), csvBuffer.getConsumedIndex(), csvBuffer.getConsumableLength());
		csvBuffer.markConsume(csvBuffer.getBufferIndex() + 1);
		currentState = NONE;
	}

	public void finish(CharBuffer csvBuffer) {
		if (!csvBuffer.isAllConsumed()) {
			newCell(csvBuffer);
		}
		handler.end();
	}

}

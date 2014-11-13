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
	
	public boolean next(CharBuffer csvBuffer) {
		while(csvBuffer.hasContent()) {
			char c = csvBuffer.getNextChar();
			switch (c) {
			case ',':
				newCellIfNotInQuote(csvBuffer);
				break;
			case '"':
				quote(csvBuffer);
				break;
			case '\n':
				if (!handleEndOfLineLF(csvBuffer)) {
					return false;
				}
				break;
			case '\r':
				if (!handleEndOfLineCR(csvBuffer)) {
					return false;
				}
				break;
			default:
				turnOffCrFlag();
			}
		}
		return true;
	}

	private int turnOffCrFlag() {
		return currentState = currentState & TURN_OFF_IN_CR_MASK;
	}

	private void newCellIfNotInQuote(CharBuffer csvBuffer) {
		if (isInQuote())
			return;
		
		newCell(csvBuffer, 1);
		turnOffCrFlag();
	}



	private boolean isInQuote() {
		return currentState == IN_QUOTE;
	}
	
	private boolean handleEndOfLineLF(CharBuffer csvBuffer) {
		boolean b = true;
		if (!isInQuote()) {
			if (currentState != IN_CR) {
				b = endOfRow(csvBuffer);
			} else {
				// we had a preceding cr so shift the marl
				csvBuffer.mark();
			}
		}
		turnOffCrFlag();
		return b;
	}

	private boolean handleEndOfLineCR(CharBuffer csvBuffer) {
		boolean b = true;
		if (!isInQuote()) {
			b = endOfRow(csvBuffer);
			currentState = IN_CR;
		}
		return b;
	}

	private boolean endOfRow(CharBuffer csvBuffer) {
		newCell(csvBuffer, 1);
		return handler.endOfRow();
	}

	private void quote(CharBuffer csvBuffer) {
		if (csvBuffer.isAllConsumed()) {
			currentState = IN_QUOTE;
		} else {
			currentState = currentState ^ ALL_QUOTES;
		}
		turnOffCrFlag();
	}

	private void newCell(CharBuffer csvBuffer, int shift) {;
		handler.newCell(csvBuffer.getCharBuffer(), csvBuffer.getMark(), csvBuffer.getLengthFromMark() - shift);
		csvBuffer.mark();
		currentState = NONE;
	}

	public void finish(CharBuffer csvBuffer) {
		if (!csvBuffer.isAllConsumed()) {
			newCell(csvBuffer, 0);
		}
		handler.end();
	}

}

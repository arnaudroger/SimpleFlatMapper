package org.sfm.csv.parser;


/**
 * Consume the charbuffer.
 */
public final class CharConsumer {

	public static final int IN_QUOTE = 4;
	public static final int IN_CR = 2;
	public static final int QUOTE = 1;
	public static final int NONE = 0;

	public static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	public static final int ALL_QUOTES = QUOTE | IN_QUOTE;

	
	private int currentState = NONE;


	public void parseFull(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		while(csvBuffer.hasContent()) {
			char c = csvBuffer.getNextChar();
			switch (c) {
			case ',':
				newCellIfNotInQuote(csvBuffer, cellConsumer);
				break;
			case '"':
				quote(csvBuffer);
				break;
			case '\n':
				handleEndOfLineLF(csvBuffer, cellConsumer);
				break;
			case '\r':
				handleEndOfLineCR(csvBuffer, cellConsumer);
				break;
			default:
				turnOffCrFlag();
			}
		}
	}

	public boolean nextLine(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		while(csvBuffer.hasContent()) {
			char c = csvBuffer.getNextChar();
			switch (c) {
				case ',':
					newCellIfNotInQuote(csvBuffer, cellConsumer);
					break;
				case '"':
					quote(csvBuffer);
					break;
				case '\n':
					if (handleEndOfLineLF(csvBuffer, cellConsumer)) {
						return true;
					}
					break;
				case '\r':
					if (handleEndOfLineCR(csvBuffer, cellConsumer)) {
						return true;
					}
					break;
				default:
					turnOffCrFlag();
			}
		}

		return false;
	}

	/**
	 * use bit mask to testing if == IN_CR
	 */
	private void turnOffCrFlag() {
		currentState = currentState & TURN_OFF_IN_CR_MASK;
	}

	private void newCellIfNotInQuote(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		if (isInQuote())
			return;

		newCell(csvBuffer, 1, cellConsumer);
		turnOffCrFlag();
	}



	private boolean isInQuote() {
		return currentState == IN_QUOTE;
	}

	/**
	 *
	 * @param csvBuffer
	 * @return true if endOfLine
	 */
	private boolean handleEndOfLineLF(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		if (!isInQuote()) {
			if (currentState != IN_CR) {
				endOfRow(csvBuffer, cellConsumer);
				return true;
			} else {
				// we had a preceding cr so shift the marl
				csvBuffer.mark();
			}
		}
		turnOffCrFlag();
		return false;
	}

	private boolean handleEndOfLineCR(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		if (!isInQuote()) {
			endOfRow(csvBuffer, cellConsumer);
			currentState = IN_CR;
			return true;
		}
		return false;
	}

	private void endOfRow(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		newCell(csvBuffer, 1, cellConsumer);
		cellConsumer.endOfRow();
	}

	private void quote(CharBuffer csvBuffer) {
		if (csvBuffer.isAllConsumed()) {
			currentState = IN_QUOTE;
		} else {
			currentState = currentState ^ ALL_QUOTES;
		}
		turnOffCrFlag();
	}

	private void newCell(CharBuffer csvBuffer, int shift, CellConsumer cellConsumer) {;
		cellConsumer.newCell(csvBuffer.getCharBuffer(), csvBuffer.getMark(), csvBuffer.getLengthFromMark() - shift);
		csvBuffer.mark();
		currentState = NONE;
	}

	public void finish(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		if (!csvBuffer.isAllConsumed()) {
			newCell(csvBuffer, 0, cellConsumer);
		}
		cellConsumer.end();
	}

}

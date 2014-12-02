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
	private CellConsumer cellConsumer;
	private CharBuffer csvBuffer;
	private int currentIndex;

	public void parseFull(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		this.cellConsumer = cellConsumer;
		this.csvBuffer = csvBuffer;

		for(; currentIndex < csvBuffer.getBufferLength(); currentIndex++) {
			consumeChar(csvBuffer.getChar(currentIndex));
		}
	}

	public void consumeChar(char c) {
		switch (c) {
        case ',':
            newCellIfNotInQuote();
            break;
        case '"':
            quote();
            break;
        case '\n':
            handleEndOfLineLF();
            break;
        case '\r':
            handleEndOfLineCR();
            break;
        default:
            turnOffCrFlag();
        }
	}

	public boolean nextLine(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		this.cellConsumer = cellConsumer;
		this.csvBuffer = csvBuffer;

		for(; currentIndex < csvBuffer.getBufferLength(); currentIndex++) {
			char c = csvBuffer.getChar(currentIndex);
			switch (c) {
				case ',':
					newCellIfNotInQuote();
					break;
				case '"':
					quote();
					break;
				case '\n':
					if (handleEndOfLineLF()) {
						currentIndex++;
						return true;
					}
					break;
				case '\r':
					if (handleEndOfLineCR()) {
						currentIndex++;
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

	private void newCellIfNotInQuote() {
		if (isInQuote())
			return;

		newCell(csvBuffer,  cellConsumer);
		turnOffCrFlag();
	}



	private boolean isInQuote() {
		return currentState == IN_QUOTE;
	}

	/**
	 *
	 * @return true if endOfLine
	 */
	private boolean handleEndOfLineLF() {
		if (!isInQuote()) {
			if (currentState != IN_CR) {
				endOfRow();
				return true;
			} else {
				// we had a preceding cr so shift the marl
				csvBuffer.mark(currentIndex + 1);
			}
		}
		turnOffCrFlag();
		return false;
	}

	private boolean handleEndOfLineCR() {
		if (!isInQuote()) {
			endOfRow();
			currentState = IN_CR;
			return true;
		}
		return false;
	}

	private void endOfRow() {
		newCell(csvBuffer,  cellConsumer);
		cellConsumer.endOfRow();
	}

	private void quote() {
		if (csvBuffer.isAllConsumed(currentIndex)) {
			currentState = IN_QUOTE;
		} else {
			currentState = currentState ^ ALL_QUOTES;
		}
		turnOffCrFlag();
	}

	private void newCell(CharBuffer csvBuffer, CellConsumer cellConsumer) {;
		cellConsumer.newCell(csvBuffer.getCharBuffer(), csvBuffer.getMark(), csvBuffer.getLengthFromMark(currentIndex));
		csvBuffer.mark(currentIndex + 1);
		currentState = NONE;
	}

	public void finish(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		if (!csvBuffer.isAllConsumed(currentIndex)) {
			newCell(csvBuffer,  cellConsumer);
		}
		cellConsumer.end();
	}

	public void shiftCurrentIndex(int mark) {
		currentIndex -= mark;
	}
}

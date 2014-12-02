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
	private int _currentIndex;

	public void parseFull(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		this.cellConsumer = cellConsumer;
		this.csvBuffer = csvBuffer;

		int bufferLength = csvBuffer.getBufferLength();
		for(int i = _currentIndex; i  < bufferLength; i++) {
			consumeChar(i, csvBuffer.getChar(i));
		}
		_currentIndex = bufferLength;
	}

	public void consumeChar(int i, char c) {
		switch (c) {
        case ',':
            newCellIfNotInQuote(i);
            break;
        case '"':
            quote(i);
            break;
        case '\n':
            handleEndOfLineLF(i);
            break;
        case '\r':
            handleEndOfLineCR(i);
            break;
        default:
            turnOffCrFlag();
        }
	}

	public boolean nextLine(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		this.cellConsumer = cellConsumer;
		this.csvBuffer = csvBuffer;

		int bufferLength = csvBuffer.getBufferLength();
		for(int i = _currentIndex; i  < bufferLength; i++) {
			char c = csvBuffer.getChar(i);
			switch (c) {
				case ',':
					newCellIfNotInQuote(i);
					break;
				case '"':
					quote(i);
					break;
				case '\n':
					if (handleEndOfLineLF(i)) {
						_currentIndex = i + 1;
						return true;
					}
					break;
				case '\r':
					if (handleEndOfLineCR(i)) {
						_currentIndex = i + 1;
						return true;
					}
					break;
				default:
					turnOffCrFlag();
			}
		}
		_currentIndex = bufferLength;

		return false;
	}

	/**
	 * use bit mask to testing if == IN_CR
	 */
	private void turnOffCrFlag() {
		currentState = currentState & TURN_OFF_IN_CR_MASK;
	}

	private void newCellIfNotInQuote(int currentIndex) {
		if (isInQuote())
			return;

		newCell(csvBuffer,  cellConsumer, currentIndex);
		turnOffCrFlag();
	}



	private boolean isInQuote() {
		return currentState == IN_QUOTE;
	}

	/**
	 *
	 * @return true if endOfLine
	 */
	private boolean handleEndOfLineLF(int currentIndex) {
		if (!isInQuote()) {
			if (currentState != IN_CR) {
				endOfRow(currentIndex);
				return true;
			} else {
				// we had a preceding cr so shift the marl
				csvBuffer.mark(currentIndex + 1);
			}
		}
		turnOffCrFlag();
		return false;
	}

	private boolean handleEndOfLineCR(int currentIndex) {
		if (!isInQuote()) {
			endOfRow(currentIndex);
			currentState = IN_CR;
			return true;
		}
		return false;
	}

	private void endOfRow(int currentIndex) {
		newCell(csvBuffer,  cellConsumer, currentIndex);
		cellConsumer.endOfRow();
	}

	private void quote(int currentIndex) {
		if (csvBuffer.isAllConsumed(currentIndex)) {
			currentState = IN_QUOTE;
		} else {
			currentState = currentState ^ ALL_QUOTES;
		}
		turnOffCrFlag();
	}

	private void newCell(CharBuffer csvBuffer, CellConsumer cellConsumer, int currentIndex) {;
		cellConsumer.newCell(csvBuffer.getCharBuffer(), csvBuffer.getMark(), csvBuffer.getLengthFromMark(currentIndex));
		csvBuffer.mark(currentIndex + 1);
		currentState = NONE;
	}

	public void finish(CharBuffer csvBuffer, CellConsumer cellConsumer) {
		if (!csvBuffer.isAllConsumed(_currentIndex)) {
			newCell(csvBuffer,  cellConsumer, _currentIndex);
		}
		cellConsumer.end();
	}

	public void shiftCurrentIndex(int mark) {
		_currentIndex -= mark;
	}
}

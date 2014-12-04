package org.sfm.csv.parser;


import java.io.IOException;
import java.io.Reader;

/**
 * Consume the charbuffer.
 */
public final class CsvCharConsumer {

	public static final int IN_QUOTE = 4;
	public static final int IN_CR = 2;
	public static final int QUOTE = 1;
	public static final int NONE = 0;

	public static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	public static final int ALL_QUOTES = QUOTE | IN_QUOTE;


	private final CharBuffer csvBuffer;
	private final char escapeChar = '"';
	private final char fieldSeparator = ',';

	private int currentState = NONE;
	private int _currentIndex;

	public CsvCharConsumer(CharBuffer csvBuffer) {
		this.csvBuffer = csvBuffer;
	}

	public void parseFull(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferLength();

		for(int i = _currentIndex; i  < bufferLength; i++) {
			char c = csvBuffer.getChar(i);
			concumeCharSwitch(c, i, cellConsumer);
		}

		_currentIndex = bufferLength;
	}

	private void concumeCharSwitch(char character, int index, CellConsumer cellConsumer) {
		switch(character) {
			case ',':
				newCellIfNotInQuote(index, cellConsumer);
				break;
			case '\n':
				handleEndOfLineLF(index, cellConsumer);
				break;
			case '\r':
				handleEndOfLineCR(index, cellConsumer);
				return;
			case '"':
				quote(index);
				break;
			default:
		}
		turnOffCrFlag();
	}

	public boolean nextLine(CellConsumer cellConsumer) {

		int bufferLength = csvBuffer.getBufferLength();
		for(int index = _currentIndex; index  < bufferLength; index++) {

			char character = csvBuffer.getChar(index);
			switch(character) {
				case ',':
					newCellIfNotInQuote(index, cellConsumer);
					break;
				case '\n':
					if (handleEndOfLineLF(index, cellConsumer)) {
						_currentIndex = index + 1;
						turnOffCrFlag();
						return true;
					}
					break;
				case '\r':
					if (handleEndOfLineCR(index, cellConsumer)) {
						_currentIndex = index + 1;
						return true;
					}
					break;
				case '"':
					quote(index);
					break;
				default:
			}
			turnOffCrFlag();
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

	private void newCellIfNotInQuote(int currentIndex, CellConsumer cellConsumer) {
		if (isInQuote())
			return;

		newCell(currentIndex, cellConsumer);
	}


	private boolean isInQuote() {
		return currentState == IN_QUOTE;
	}

	/**
	 *
	 * @return true if endOfLine
	 */
	private boolean handleEndOfLineLF(int currentIndex, CellConsumer cellConsumer) {
		if (!isInQuote()) {
			if (currentState != IN_CR) {
				endOfRow(currentIndex, cellConsumer);
				return true;
			} else {
				// we had a preceding cr so shift the marl
				csvBuffer.mark(currentIndex + 1);
			}
		}
		return false;
	}

	private boolean handleEndOfLineCR(int currentIndex, CellConsumer cellConsumer) {
		if (!isInQuote()) {
			endOfRow(currentIndex, cellConsumer);
			currentState = IN_CR;
			return true;
		}
		return false;
	}

	private void endOfRow(int currentIndex, CellConsumer cellConsumer) {
		newCell(currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}

	private void quote(int currentIndex) {
		if (isAllConsumedFromMark(currentIndex)) {
			currentState = IN_QUOTE;
		} else {
			currentState = currentState ^ ALL_QUOTES;
		}
	}

	private void newCell(int currentIndex, CellConsumer cellConsumer) {;
		cellConsumer.newCell(csvBuffer.getCharBuffer(), csvBuffer.getMark(), currentIndex - csvBuffer.getMark());
		csvBuffer.mark(currentIndex + 1);
		currentState = NONE;
	}

	public void finish(CellConsumer cellConsumer) {
		if (!isAllConsumedFromMark(_currentIndex)) {
			newCell(_currentIndex, cellConsumer);
		}
		cellConsumer.end();
	}

	public void shiftCurrentIndex(int mark) {
		_currentIndex -= mark;
	}

	public boolean fillBuffer(Reader reader) throws IOException {
		shiftCurrentIndex(csvBuffer.shiftBufferToMark());
		return csvBuffer.fillBuffer(reader);
	}

	public boolean isAllConsumedFromMark(int bufferIndex) {
		return csvBuffer.getMark() >= bufferIndex -1 ;
	}
}

package org.sfm.csv.parser;


import java.io.IOException;
import java.io.Reader;

/**
 * Consume the charbuffer.
 */
public final class StandardCsvCharConsumer extends AbstractCsvCharConsumer {

	public StandardCsvCharConsumer(CharBuffer csvBuffer) {
		super(csvBuffer);
	}

	protected void consumeOneChar(char character, int index, CellConsumer cellConsumer) {
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

	@Override
	public boolean nextRow(CellConsumer cellConsumer) {

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


}

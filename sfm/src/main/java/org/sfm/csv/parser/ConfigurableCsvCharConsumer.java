package org.sfm.csv.parser;


/**
 * Consume the charBuffer.
 */
public final class ConfigurableCsvCharConsumer extends AbstractCsvCharConsumer {

	private final char separatorChar;

	public ConfigurableCsvCharConsumer(CharBuffer csvBuffer, char separatorChar, char quoteChar) {
		super(csvBuffer, quoteChar);
		this.separatorChar = separatorChar;
	}


	@Override
	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.bufferSize;
		char[] chars = csvBuffer.buffer;
		int currentIndex = _currentIndex;
		while(currentIndex  < bufferLength) {
			consumeOneChar(currentIndex, chars[currentIndex], cellConsumer);
			currentIndex++;
		}
		_currentIndex = currentIndex;
	}

	private void consumeOneChar(int currentIndex, char character, CellConsumer cellConsumer) {
		if (character == separatorChar) {
			newCellIfNotInQuote(currentIndex, cellConsumer);
		} else if (character ==  '\n') {
				handleEndOfLineLF(currentIndex, cellConsumer);
		} else if (character == '\r') {
			handleEndOfLineCR(currentIndex, cellConsumer);
			return;
		} else if (character == quoteChar) {
			quote(currentIndex);
		}
		turnOffCrFlag();
	}

	@Override
	public boolean consumeToNextRow(CellConsumer cellConsumer) {

		int bufferLength = csvBuffer.getBufferSize();
		char[] buffer = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		for(;currentIndex  < bufferLength; currentIndex++) {
			char character = buffer[currentIndex];

			if (character == separatorChar) {
				newCellIfNotInQuote(currentIndex, cellConsumer);
			} else if (character ==  '\n') {
				if (handleEndOfLineLF(currentIndex, cellConsumer)) {
					_currentIndex = currentIndex + 1;
					turnOffCrFlag();
					return true;
				}
			} else if (character == '\r') {
				if (handleEndOfLineCR(currentIndex, cellConsumer)) {
					_currentIndex = currentIndex + 1;
					return true;
				}
			} else if (character == quoteChar) {
				quote(currentIndex);
			}
			turnOffCrFlag();
		}
		_currentIndex = currentIndex;
		return false;
	}
}

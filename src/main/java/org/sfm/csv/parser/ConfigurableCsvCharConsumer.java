package org.sfm.csv.parser;


/**
 * Consume the charbuffer.
 */
public final class ConfigurableCsvCharConsumer extends AbstractCsvCharConsumer {

	private final char separatoChar;
	private final char quoteChar;

	public ConfigurableCsvCharConsumer(CharBuffer csvBuffer, char separatoChar, char quoteChar) {
		super(csvBuffer);
		this.separatoChar = separatoChar;
		this.quoteChar = quoteChar;
	}

	protected void consumeOneChar(char character, int index, CellConsumer cellConsumer) {
		if (character == separatoChar) {
			newCellIfNotInQuote(index, cellConsumer);
		} else if (character ==  '\n') {
				handleEndOfLineLF(index, cellConsumer);
		} else if (character == '\r') {
			handleEndOfLineCR(index, cellConsumer);
			return;
		} else if (character == quoteChar) {
			quote(index);
		}
		turnOffCrFlag();
	}

	@Override
	public boolean nextRow(CellConsumer cellConsumer) {

		int bufferLength = csvBuffer.getBufferLength();
		for(int index = _currentIndex; index  < bufferLength; index++) {

			char character = csvBuffer.getChar(index);

			if (character == separatoChar) {
				newCellIfNotInQuote(index, cellConsumer);
			} else if (character ==  '\n') {
				if (handleEndOfLineLF(index, cellConsumer)) {
					_currentIndex = index + 1;
					turnOffCrFlag();
					return true;
				}
			} else if (character == '\r') {
				if (handleEndOfLineCR(index, cellConsumer)) {
					_currentIndex = index + 1;
					return true;
				}
			} else if (character == quoteChar) {
				quote(index);
			}
			turnOffCrFlag();
		}
		_currentIndex = bufferLength;

		return false;
	}

	@Override
	public char quoteChar() {
		return quoteChar;
	}


}

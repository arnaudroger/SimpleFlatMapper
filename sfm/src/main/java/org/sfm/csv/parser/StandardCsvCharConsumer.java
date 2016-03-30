package org.sfm.csv.parser;


/**
 * Consume the charBuffer.
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
	public boolean consumeToNextRow(CellConsumer cellConsumer) {

		int bufferLength = csvBuffer.getBufferSize();
		char[] buffer = csvBuffer.getCharBuffer();
		for(int index = _currentIndex; index  < bufferLength; index++) {

			char character = buffer[index];
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

	@Override
	public final char quoteChar() {
		return '"';
	}


}

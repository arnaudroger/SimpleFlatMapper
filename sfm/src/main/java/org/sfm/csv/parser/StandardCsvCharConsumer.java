package org.sfm.csv.parser;


/**
 * Consume the charBuffer.
 */
public final class StandardCsvCharConsumer extends AbstractCsvCharConsumer {

	public StandardCsvCharConsumer(CharBuffer csvBuffer) {
		super(csvBuffer, '"');
	}

	@Override
	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		while(currentIndex  < bufferLength) {
			consumeOneChar(currentIndex, chars[currentIndex], cellConsumer);
			currentIndex++;
		}
		_currentIndex = currentIndex;
	}

	private void consumeOneChar(int currentIndex, char character, CellConsumer cellConsumer) {
		switch(character) {
			case ',':
				newCellIfNotInQuote(currentIndex, cellConsumer);
				break;
			case '\n':
				handleEndOfLineLF(currentIndex, cellConsumer);
				break;
			case '\r':
				handleEndOfLineCR(currentIndex, cellConsumer);
				return;
			case '"':
				quote(currentIndex);
				break;
			default:
		}
		turnOffCrFlag();
	}

	@Override
	public boolean consumeToNextRow(CellConsumer cellConsumer) {

		int bufferLength = csvBuffer.getBufferSize();
		char[] buffer = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		while(currentIndex  < bufferLength) {
			char character = buffer[currentIndex];
			switch(character) {
				case ',':
					newCellIfNotInQuote(currentIndex, cellConsumer);
					break;
				case '\n':
					if (handleEndOfLineLF(currentIndex, cellConsumer)) {
						_currentIndex = currentIndex + 1;
						turnOffCrFlag();
						return true;
					}
					break;
				case '\r':
					if (handleEndOfLineCR(currentIndex, cellConsumer)) {
						_currentIndex = currentIndex + 1;
						return true;
					}
					break;
				case '"':
					quote(currentIndex);
					break;
				default:
			}
			turnOffCrFlag();
			currentIndex++;
		}
		this._currentIndex = currentIndex;
		return false;
	}
}

package org.simpleflatmapper.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public abstract class CharConsumer {

	protected static final int LAST_CHAR_WAS_SEPARATOR = 4;
	protected static final int LAST_CHAR_WAS_CR = 2;
	protected static final int ESCAPED = 1;
	protected static final int NONE = 0;
	protected static final int TURN_OFF_LAST_CHAR_MASK = ~(LAST_CHAR_WAS_CR|LAST_CHAR_WAS_SEPARATOR);

	protected final CharBuffer csvBuffer;
	protected int _currentIndex;
	protected int _currentState = NONE;

	public CharConsumer(CharBuffer csvBuffer) {
		this.csvBuffer = csvBuffer;
	}

	public abstract void consumeAllBuffer(CellConsumer cellConsumer);

	public abstract boolean consumeToNextRow(CellConsumer cellConsumer);

	protected abstract void newCell(int end, final CellConsumer cellConsumer);

	protected final boolean endOfRow(int currentIndex, CellConsumer cellConsumer, int state) {
		endOfRow(currentIndex, cellConsumer);
		_currentState = state;
		_currentIndex = currentIndex + 1;
		return true;
	}

	protected final void endOfRow(int currentIndex, CellConsumer cellConsumer) {
		newCell(currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}

	public final void finish(CellConsumer cellConsumer) {
		if ( _currentIndex > csvBuffer.getMark() || (_currentState & LAST_CHAR_WAS_SEPARATOR) != 0) {
			newCell(_currentIndex, cellConsumer);
		}
		cellConsumer.end();
	}

	public final int refillBuffer() throws IOException {
		_currentIndex -= csvBuffer.shiftBufferToMark();
		return csvBuffer.fillBuffer();
	}

}

package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public final class CharBuffer {


	private char[] buffer;
	private int bufferLength;
	

	private int bufferIndex;
	
	private boolean stop;
	private int consumableStartIndex;

	public CharBuffer(final int bufferSize) {
		this.buffer = new char[bufferSize];
	}
	
	public void markConsume(int index) {
		this.consumableStartIndex = index;
	}
	public void markStop() {
		this.stop = true;
	}
	
	public boolean fillBuffer(Reader reader) throws IOException {
		
		shiftBufferToConsumedIndex();
		
		int length = reader.read(buffer, bufferLength, buffer.length- bufferLength);
		if (length != -1) {
			bufferLength += length;
			return true;
		} else {
			return false;
		}
	}
	
	public void consumeBytes(CharConsumer consumer) {
		while (isNotStopped() && bufferIndex < bufferLength) {
			consumer.handleChar(this);
			bufferIndex++;
		}
	}

	private boolean isNotStopped() {
		return !stop;
	}

	/**
	 * parse cvs
	 * 
	 * @return
	 * @throws IOException
	 */
	public void parse(Reader reader, CharConsumer consumer)
			throws IOException {
		unstop();
		
		do {
			consumeBytes(consumer);
			
			if (isStopped()) return;
			
		} while(fillBuffer(reader));
		
		consumer.finish(this);
		
	}

	private boolean isStopped() {
		return stop;
	}

	private void unstop() {
		stop = false;
	}

	private void shiftBufferToConsumedIndex() {
		// shift buffer consumer data
		int newLength = Math.max(bufferLength - consumableStartIndex, 0);

		// if buffer tight double the size
		if (newLength <= (bufferLength >> 1)) {
			System.arraycopy(buffer, consumableStartIndex, buffer, 0, newLength);
		} else {
			// double buffer size
			char[] newbuffer = new char[buffer.length << 1];
			System.arraycopy(buffer, consumableStartIndex, newbuffer, 0, newLength);
			buffer = newbuffer;
		}
		bufferIndex = bufferIndex - consumableStartIndex;
		bufferLength = newLength;
		
		consumableStartIndex = 0;
	}

	public char getCurrentChar() {
		return buffer[bufferIndex];
	}

	public char[] getCharBuffer() {
		return buffer;
	}

	public int getBufferIndex() {
		return bufferIndex;
	}

	public boolean isAllConsumed() {
		return consumableStartIndex >= bufferIndex;
	}

	public int getConsumedIndex() {
		return consumableStartIndex;
	}

	public int getConsumableLength() {
		return bufferIndex - consumableStartIndex;
	}
}

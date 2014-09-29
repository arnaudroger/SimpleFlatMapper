package org.sfm.text;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public final class CharBufferDecoder implements StringDecoder {

	private final Charset charset;
	private final CharBuffer buffer = CharBuffer.allocate(4096);
	private final CharsetDecoder decoder;
	public CharBufferDecoder(Charset charset) {
		this.charset = charset;
		this.decoder = charset.newDecoder();
	}

	@Override
	public String decode(byte[] bytes, int offset, int length) {
		if (length > buffer.capacity()) {
			return new String(bytes, offset, length, charset);
		} else {
			buffer.clear();
			decoder.decode(ByteBuffer.wrap(bytes,  offset,  length), buffer, true);
			return new String(buffer.array(), 0, buffer.limit());
		}
	}

}

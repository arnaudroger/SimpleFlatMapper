package org.sfm.text;

import java.nio.charset.Charset;

import sun.nio.cs.ArrayDecoder;

@SuppressWarnings("restriction")
public final class ArrayDecoderImpl implements StringDecoder {

	private final sun.nio.cs.ArrayDecoder decoder;
	private final Charset charset;
	private final char[] buffer = new char[4096];
	
	public ArrayDecoderImpl(Charset charset) {
		this.decoder = (ArrayDecoder) charset.newDecoder();
		this.charset = charset;
	}
	
	@Override
	public String decode(byte[] bytes, int offset, int length) {
		if (length <= buffer.length) {
			int l = decoder.decode(bytes, offset, length, buffer);
			return new String(buffer, 0, l);
		} else {
			return new String(bytes, offset, length, charset);
		}
	}
	
	public static boolean isEligible(Charset charset) {
		return charset.newDecoder() instanceof sun.nio.cs.ArrayDecoder;
	}

}

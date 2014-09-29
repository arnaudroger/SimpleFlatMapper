package org.sfm.csv;

import java.nio.charset.Charset;

import org.sfm.text.StringDecoder;
import org.sfm.text.StringDecoderFactory;

public final class DecoderContext {
	private final StringDecoder stringDecoder;
	private final Charset charset;
	
	public DecoderContext(Charset charset) {
		this.charset = charset;
		if (charset != null) {
			this.stringDecoder = StringDecoderFactory.newStringDecoder(charset);
		} else {
			this.stringDecoder = null;
		}
	}
	
	
	public StringDecoder getStringDecoder() {
		return stringDecoder;
	}
	
	public Charset getCharset() {
		return charset;
	}


	public static DecoderContext forCharset(String charset) {
		return new DecoderContext(Charset.forName("UTF-8"));
	}
	
	
}

package org.sfm.text;

import java.nio.charset.Charset;

public final class StringDecoderFactory {
	
	private static final boolean arrayDecoder;
	
	static {
		boolean ad = false;
		try {
			Class.forName("sun.nio.cs.ArrayDecoder");
			ad = true;
		} catch(Exception e){
		}
		arrayDecoder = ad;
	}
	
	public static final  StringDecoder newStringDecoder(Charset charset) {
		if (arrayDecoder) {
			if (ArrayDecoderImpl.isEligible(charset)) {
				return new ArrayDecoderImpl(charset);
			} else {
				return new CharBufferDecoder(charset);
			}
		} else {
			return new CharBufferDecoder(charset);
		}
	}
	
}

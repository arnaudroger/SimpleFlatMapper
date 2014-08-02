package org.sfm.text;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Map.Entry;

public class CharsetCheck {
	public static void main(String[] args) throws CharacterCodingException {
		
		for(Entry<String, Charset> e : Charset.availableCharsets().entrySet()) {
			Charset charset = e.getValue();
			
			try {
				CharsetEncoder encoder = charset
						.newEncoder()
						.onMalformedInput(CodingErrorAction.REPORT)
						.onUnmappableCharacter(CodingErrorAction.REPORT);
				
				checkCharHasSameCode(',', charset, encoder);
				checkCharHasSameCode('"', charset, encoder);
				checkCharHasSameCode('\n', charset, encoder);
			} catch(Exception ee) {
				System.out.println(charset.name() + " unsupported " + ee.getMessage());
			}
		}
	}

	private static void checkCharHasSameCode(char c, Charset charset,
			CharsetEncoder encoder) throws CharacterCodingException {
		byte[] bytes = new String(new char[] {c}).getBytes(charset);
		if (bytes.length > 1) {
			if (c == bytes[0]) {
				System.out.println(charset.name() + " " + toString(c) + " kind of compatible " + Arrays.toString(bytes));
			} else {
				System.out.println(charset.name() + " " +  toString(c) + " not compatible " + Arrays.toString(bytes));
			}
		} else {
			if (c == bytes[0]) {
				System.out.println(charset.name() + " " +  toString(c) + " compatible "+ Arrays.toString(bytes));
			} else {
				System.out.println(charset.name() + " " +  toString(c) + " != encoded to " + Arrays.toString(bytes) );
			}
		}
	}

	private static String toString(char c) {
		if (c == '\n') return "'\\n'";
		return "'" + c + "'";
	}
}

package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.DecoderContext;

public class BooleanCellValueReader implements CellValueReader<Boolean> {

	@Override
	public Boolean read(byte[] bytes, int offset, int length, DecoderContext decoderContext) {
		return new Boolean(parseBoolean(bytes, offset, length));
	}
	
	@Override
	public Boolean read(char[] chars, int offset, int length) {
		return new Boolean(parseBoolean(chars, offset, length));
	}

	public static boolean parseBoolean(byte[] bytes, int offset, int length) {
		switch (length) {
		case 0:
			return false;
		case 1:
			switch (bytes[offset]) {
			case 0:
			case '0':
			case 'F':
			case 'f':
			case 'n':
			case 'N':
				return false;
			default:
				return true;
			}
		case 2:
			if ((bytes[offset] == 'N' || bytes[offset] == 'n')
				&& (bytes[offset + 1] == 'O' || bytes[offset + 1] == 'o')) {
				return false;
			}
		case 5:
			if (
				(bytes[offset] == 'F' || bytes[offset] == 'f')
				&& (bytes[offset + 1] == 'A' || bytes[offset + 1] == 'a')
				&& (bytes[offset + 2] == 'L' || bytes[offset + 2] == 'l')
				&& (bytes[offset + 3] == 'S' || bytes[offset + 3] == 's')
				&& (bytes[offset + 4] == 'E' || bytes[offset + 4] == 'e')
				) {
					return false;
				}
		}
		return true;
	}
	
	public static boolean parseBoolean(char[] chars, int offset, int length) {
		switch (length) {
		case 0:
			return false;
		case 1:
			switch (chars[offset]) {
			case 0:
			case '0':
			case 'F':
			case 'f':
			case 'n':
			case 'N':
				return false;
			default:
				return true;
			}
		case 2:
			if ((chars[offset] == 'N' || chars[offset] == 'n')
				&& (chars[offset + 1] == 'O' || chars[offset + 1] == 'o')) {
				return false;
			}
		case 5:
			if (
				(chars[offset] == 'F' || chars[offset] == 'f')
				&& (chars[offset + 1] == 'A' || chars[offset + 1] == 'a')
				&& (chars[offset + 2] == 'L' || chars[offset + 2] == 'l')
				&& (chars[offset + 3] == 'S' || chars[offset + 3] == 's')
				&& (chars[offset + 4] == 'E' || chars[offset + 4] == 'e')
				) {
					return false;
				}
		}
		return true;
	}



}

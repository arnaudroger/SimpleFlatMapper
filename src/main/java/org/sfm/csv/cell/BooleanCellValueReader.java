package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class BooleanCellValueReader implements CellValueReader<Boolean> {

	@Override
	public Boolean read(byte[] bytes, int offset, int length) {

		switch (length) {
		case 0:
			return Boolean.FALSE;
		case 1:
			switch (bytes[offset]) {
			case 0:
			case '0':
			case 'F':
			case 'f':
			case 'n':
			case 'N':
				return Boolean.FALSE;
			default:
				return Boolean.TRUE;
			}
		case 2:
			if ((bytes[offset] == 'N' || bytes[offset] == 'n')
				&& (bytes[offset + 1] == 'O' || bytes[offset + 1] == 'o')) {
				return Boolean.FALSE;
			}
		case 5:
			if (
				(bytes[offset] == 'F' || bytes[offset] == 'f')
				&& (bytes[offset + 1] == 'A' || bytes[offset + 1] == 'a')
				&& (bytes[offset + 2] == 'L' || bytes[offset + 2] == 'l')
				&& (bytes[offset + 3] == 'S' || bytes[offset + 3] == 's')
				&& (bytes[offset + 4] == 'E' || bytes[offset + 4] == 'e')
				) {
					return Boolean.FALSE;
				}
		default: return Boolean.TRUE;
		}
	}

}

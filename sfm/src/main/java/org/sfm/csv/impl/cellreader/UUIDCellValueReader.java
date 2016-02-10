package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;
import org.sfm.map.ParsingContextProvider;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class UUIDCellValueReader implements CellValueReader<UUID> {

	public UUIDCellValueReader() {
	}
	
	@Override
	public UUID read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length > 0) {
			return UUID.fromString(StringCellValueReader.readString(chars, offset, length));
		}
		return null;
	}


    @Override
    public String toString() {
        return "UUIDCellValueReader{}";
    }
}

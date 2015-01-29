package org.sfm.csv.impl;

import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.map.impl.ColumnsMapperKey;

import java.util.ArrayList;
import java.util.List;

public final class ColumnsMapperKeyBuilderCellConsumer<T> implements CellConsumer {
	/**
	 * 
	 */
	private final List<String> columns = new ArrayList<String>();

	public ColumnsMapperKeyBuilderCellConsumer() {
	}

	@Override
	public void endOfRow() {
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		columns.add(StringCellValueReader.readString(chars, offset, length));
	}

	@Override
	public void end() {
	}

	public ColumnsMapperKey getKey() {
		return new ColumnsMapperKey(columns.toArray(new String[columns.size()]));
	}
}
package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.map.mapper.MapperKey;

import java.util.ArrayList;
import java.util.List;

public final class ColumnsMapperKeyBuilderCellConsumer implements CellConsumer {
	/**
	 * 
	 */
	private final List<CsvColumnKey> columns = new ArrayList<CsvColumnKey>();

	private int index = 0;

	public ColumnsMapperKeyBuilderCellConsumer() {
	}

	@Override
	public void endOfRow() {
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		columns.add(new CsvColumnKey(StringCellValueReader.readString(chars, offset, length), index));
		index++;
	}

	@Override
	public void end() {
	}

	public MapperKey<CsvColumnKey> getKey() {
		return new MapperKey<CsvColumnKey>(columns.toArray(new CsvColumnKey[columns.size()]));
	}
}
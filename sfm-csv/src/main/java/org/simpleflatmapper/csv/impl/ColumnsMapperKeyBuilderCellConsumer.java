package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.map.mapper.MapperKey;

import java.util.ArrayList;
import java.util.List;

public final class ColumnsMapperKeyBuilderCellConsumer implements CellConsumer {
	/**
	 * 
	 */
	private final List<CsvColumnKey> columns = new ArrayList<CsvColumnKey>();

	private int index = 0;
	private boolean hasNoData = true;

	public ColumnsMapperKeyBuilderCellConsumer() {
	}

	@Override
	public boolean endOfRow() {
		hasNoData = false;
		return true;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (!hasNoData) throw new IllegalArgumentException("Already consume the headers");
		columns.add(new CsvColumnKey(new String(chars, offset, length), index));
		index++;
	}

	@Override
	public void end() {
	}

	public MapperKey<CsvColumnKey> getKey() {
		return new MapperKey<CsvColumnKey>(columns.toArray(new CsvColumnKey[0]));
	}

	public boolean hasNoData() {
		return hasNoData;
	}
}
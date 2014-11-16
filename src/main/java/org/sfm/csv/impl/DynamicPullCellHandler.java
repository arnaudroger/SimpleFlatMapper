package org.sfm.csv.impl;

import java.util.ArrayList;
import java.util.List;

import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.utils.RowHandler;

public final class DynamicPullCellHandler<T> implements CharsCellHandler {
	/**
	 * 
	 */
	private final DynamicCsvMapper<T> dynamicCsvMapper;
	private final RowHandler<T> handle;
	private final int rowStart;

	private CsvMapperCellHandler<T> cellHandler;
	private List<String> columns = new ArrayList<String>();
	private int currentRow;
	
	public DynamicPullCellHandler(DynamicCsvMapper<T> dynamicCsvMapper, RowHandler<T> handle, int rowStart) {
		this.dynamicCsvMapper = dynamicCsvMapper;
		this.handle = handle;
		this.rowStart = rowStart;
	}

	@Override
	public boolean endOfRow() {
		boolean b = cellHandler == null;
		if (rowStart == -1 || currentRow >= rowStart) {
			if (cellHandler == null) {
				ColumnsMapperKey key = new ColumnsMapperKey(columns.toArray(new String[columns.size()]));
				CsvMapperImpl<T> csvMapperImpl = dynamicCsvMapper.getCsvMapper(key);
				cellHandler = csvMapperImpl.newCellHandler(handle);
			} else {
				cellHandler.endOfRow();
			}
		}
		currentRow ++;
		return b;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (rowStart == -1 || currentRow >= rowStart) {
			if (cellHandler == null) {
				columns.add(StringCellValueReader.readString(chars, offset, length));
			} else {
				cellHandler.newCell(chars, offset, length);
			}
		}
	}

	@Override
	public void end() {
		if (cellHandler != null) {
			cellHandler.end();
		}
	}
}
package org.sfm.csv.impl;

import java.util.ArrayList;
import java.util.List;

import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.utils.RowHandler;

public final class DynamicPushCellHandler<T> implements CharsCellHandler {
	/**
	 * 
	 */
	private final DynamicCsvMapper<T> dynamicCsvMapper;
	private final RowHandler<T> handle;
	private final int rowStart;
	private final int limit;

	private CsvMapperCellHandler<T> cellHandler;
	private List<String> columns = new ArrayList<String>();
	private int currentRow;
	
	public DynamicPushCellHandler(DynamicCsvMapper<T> dynamicCsvMapper, RowHandler<T> handle, int rowStart, int limit) {
		this.dynamicCsvMapper = dynamicCsvMapper;
		this.handle = handle;
		this.rowStart = rowStart;
		this.limit = limit;
	}

	@Override
	public boolean endOfRow() {
		if (rowStart == -1 || currentRow >= rowStart) {
			if (cellHandler == null) {
				ColumnsMapperKey key = new ColumnsMapperKey(columns.toArray(new String[columns.size()]));
				CsvMapperImpl<T> csvMapperImpl = dynamicCsvMapper.getCsvMapper(key);
				cellHandler = csvMapperImpl.newCellHandler(handle);
			} else {
				cellHandler.endOfRow();
			}
		}
		return continueProcessing();
	}

	private boolean continueProcessing() {
		boolean continueProcessing =  limit == -1 || (currentRow - rowStart) < limit;
		currentRow++;
		return continueProcessing;
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
package org.sfm.csv.parser;

import java.util.ArrayList;
import java.util.List;

import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.utils.RowHandler;

public final class StringArrayHandler implements CharsCellHandler {
	private final RowHandler<String[]> handler;
	private final List<String> currentRow = new ArrayList<String>(10);

	public StringArrayHandler(RowHandler<String[]> handler) {
		this.handler = handler;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		String str = StringCellValueReader.readString(chars, offset, length);
		currentRow.add(str);
	}

	@Override
	public boolean endOfRow() {
		try {
			handler.handle(currentRow.toArray(new String[currentRow.size()]));
			currentRow.clear();
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void end() {
		if (!currentRow.isEmpty()) {
			endOfRow();
		}
	}
}
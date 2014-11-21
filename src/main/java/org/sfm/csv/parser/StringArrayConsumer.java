package org.sfm.csv.parser;

import java.util.ArrayList;
import java.util.List;

import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.utils.RowHandler;

public final class StringArrayConsumer<RH extends RowHandler<String[]>> implements CellConsumer {
	private final RH handler;
	private final List<String> currentRow = new ArrayList<String>(10);

	public StringArrayConsumer(RH handler) {
		this.handler = handler;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		String str = StringCellValueReader.readString(chars, offset, length);
		currentRow.add(str);
	}

	@Override
	public void endOfRow() {
		try {
			handler.handle(currentRow.toArray(new String[currentRow.size()]));
			currentRow.clear();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public RH handler() {
		return handler;
	}

	@Override
	public void end() {
		if (!currentRow.isEmpty()) {
			endOfRow();
		}
	}

	public static <RH extends RowHandler<String[]>> StringArrayConsumer<RH> newInstance(RH handler) {
		return new StringArrayConsumer<RH>(handler);
	}
}
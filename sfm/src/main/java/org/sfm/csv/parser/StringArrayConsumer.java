package org.sfm.csv.parser;

import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.util.Arrays;

public final class StringArrayConsumer<RH extends RowHandler<String[]>> implements CellConsumer {
	private final RH handler;
	private String[] currentRow = new String[8];
	private int currentIndex;


	public StringArrayConsumer(RH handler) {
		this.handler = handler;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (currentIndex >= currentRow.length) {
			currentRow = Arrays.copyOf(currentRow, currentRow.length * 2);
		}
		currentRow[currentIndex++] = StringCellValueReader.readString(chars, offset, length);
	}

	@Override
	public void endOfRow() {
		try {
			String[] result = Arrays.copyOf(currentRow, currentIndex);
			Arrays.fill(currentRow, null);
			handler.handle(result);
			currentIndex = 0;
		} catch (Exception e) {
            ErrorHelper.rethrow(e);
		}
	}

	public RH handler() {
		return handler;
	}

	@Override
	public void end() {
		if (!isEmpty()) {
			endOfRow();
		}
	}

	private boolean isEmpty() {
		return currentIndex == 0;
	}

	public static <RH extends RowHandler<String[]>> StringArrayConsumer<RH> newInstance(RH handler) {
		return new StringArrayConsumer<RH>(handler);
	}
}
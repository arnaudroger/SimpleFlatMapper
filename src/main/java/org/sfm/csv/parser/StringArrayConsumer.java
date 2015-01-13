package org.sfm.csv.parser;

import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.utils.RowHandler;

public final class StringArrayConsumer<RH extends RowHandler<String[]>> implements CellConsumer {
	private final RH handler;
	private String[] currentRow = new String[10];
	private int currentIndex;


	public StringArrayConsumer(RH handler) {
		this.handler = handler;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (currentIndex >= currentRow.length) {
			doubleSize();
		}
		currentRow[currentIndex++] = StringCellValueReader.readString(chars, offset, length);
	}

	private void doubleSize() {
		String[] newArray = new String[currentRow.length * 2];
		System.arraycopy(currentRow, 0, newArray, 0, currentIndex);
		currentRow = newArray;
	}

	@Override
	public void endOfRow() {
		try {
			String[] result = new String[currentIndex];
			System.arraycopy(currentRow, 0, result, 0, currentIndex);
			handler.handle(result);
			currentIndex = 0;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
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
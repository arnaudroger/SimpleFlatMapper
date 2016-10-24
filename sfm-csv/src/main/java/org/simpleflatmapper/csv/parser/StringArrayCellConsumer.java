package org.simpleflatmapper.csv.parser;

import org.simpleflatmapper.csv.impl.cellreader.StringCellValueReader;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ErrorHelper;
import java.util.Arrays;

public final class StringArrayCellConsumer<RH extends CheckedConsumer<? super String[]>> implements CellConsumer {

	public static final int DEFAULT_MAX_NUMBER_OF_CELL_PER_ROW = 64 * 1024 * 1024;
	private final RH handler;
	private final int maxNumberOfCellPerRow;
	private int currentIndex;
	private int currentLength = 8;
	private String[] currentRow = new String[currentLength];

	private StringArrayCellConsumer(RH handler, int maxNumberOfCellPerRow) {
		this.handler = handler;
		this.maxNumberOfCellPerRow = maxNumberOfCellPerRow;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		ensureCapacity();
		String cellValue = StringCellValueReader.readString(chars, offset, length);
		currentRow[currentIndex] = cellValue;
		currentIndex ++;
	}

	private void ensureCapacity() {
		if (currentIndex >= currentLength) {
			if (currentLength >= maxNumberOfCellPerRow) {
				throw new ArrayIndexOutOfBoundsException("Reach maximum number of cell per row " + currentIndex);
			}
			currentLength *= 2;
			currentRow = Arrays.copyOf(currentRow, currentLength);
		}
	}

	@Override
	public boolean endOfRow() {
		try {
			return _endOfRow();
		} catch (Exception e) { return ErrorHelper.<Boolean>rethrow(e);  }
	}

	private boolean _endOfRow() throws Exception {
		handler.accept(Arrays.copyOf(currentRow, currentIndex));
		Arrays.fill(currentRow, 0, currentIndex, null);
		currentIndex = 0;
		return true;
	}

	public RH handler() {
		return handler;
	}

	@Override
	public void end() {
		if (currentIndex > 0) {
			endOfRow();
		}
	}
	public static <RH extends CheckedConsumer<? super String[]>> StringArrayCellConsumer<RH> newInstance(RH handler, int maxNumberOfCellPerRow) {
		return new StringArrayCellConsumer<RH>(handler, maxNumberOfCellPerRow);
	}

	public static <RH extends CheckedConsumer<? super String[]>> StringArrayCellConsumer<RH> newInstance(RH handler) {
		return newInstance(handler, DEFAULT_MAX_NUMBER_OF_CELL_PER_ROW);
	}
}
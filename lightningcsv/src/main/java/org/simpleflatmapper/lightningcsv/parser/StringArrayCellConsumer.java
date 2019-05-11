package org.simpleflatmapper.lightningcsv.parser;

import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ErrorHelper;
import java.util.Arrays;

public final class StringArrayCellConsumer<RH extends CheckedConsumer<? super String[]>> implements CellConsumer {

	public static final int DEFAULT_MAX_NUMBER_OF_CELL_PER_ROW = 64 * 1024 * 1024;
	private final RH handler;
	private final int maxNumberOfCellPerRow;
	private int currentIndex;
	private String[] currentRow = new String[8];

	private StringArrayCellConsumer(RH handler, int maxNumberOfCellPerRow) {
		this.handler = handler;
		this.maxNumberOfCellPerRow = maxNumberOfCellPerRow;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		ensureCapacity();
		currentRow[currentIndex] = length > 0 ? new String(chars, offset, length) : "";
		currentIndex ++;
	}

	private void ensureCapacity() {
		if (currentIndex >= currentRow.length) {
			if (currentRow.length >= maxNumberOfCellPerRow) {
				throw new ArrayIndexOutOfBoundsException("Reach maximum number of cell per row " + currentIndex);
			}
			currentRow = Arrays.copyOf(currentRow, currentRow.length * 2);
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
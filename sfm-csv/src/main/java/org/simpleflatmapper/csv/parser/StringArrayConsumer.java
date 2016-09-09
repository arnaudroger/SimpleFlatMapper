package org.simpleflatmapper.csv.parser;

import org.simpleflatmapper.csv.impl.cellreader.StringCellValueReader;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.RowHandler;

import java.lang.reflect.Array;
import java.util.Arrays;

public final class StringArrayConsumer<RH extends RowHandler<String[]>> implements CellConsumer {

	public static final int DEFAULT_MAX_NUMBER_OF_CELL_PER_ROW = 64 * 1024 * 1024;
	private final RH handler;
	private final int maxNumberOfCellPerRow;
	private int currentIndex;
	private int currentLength = 8;
	private String[] currentRow = new String[currentLength];


	public StringArrayConsumer(RH handler) {
		this(handler, DEFAULT_MAX_NUMBER_OF_CELL_PER_ROW);
	}
	public StringArrayConsumer(RH handler, int maxNumberOfCellPerRow) {
		this.handler = handler;
		this.maxNumberOfCellPerRow = maxNumberOfCellPerRow;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		ensureCapacity();
		currentRow[currentIndex] = StringCellValueReader.readString(chars, offset, length);
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
	public void endOfRow() {
		try {
			_endOfRow();
		} catch (Exception e) { ErrorHelper.rethrow(e); }
	}

	private void _endOfRow() throws Exception {
		handler.handle(Arrays.copyOf(currentRow, currentIndex));
		Arrays.fill(currentRow, 0, currentIndex, null);
		currentIndex = 0;
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

	public static <RH extends RowHandler<String[]>> StringArrayConsumer<RH> newInstance(RH handler) {
		return new StringArrayConsumer<RH>(handler);
	}
}
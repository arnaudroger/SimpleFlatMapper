package org.simpleflatmapper.csv.parser;

import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.csv.CsvReader;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.ListCollector;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public final class YamlCommentStringArrayConsumer<RC extends CheckedConsumer<? super String[]>, CC extends CheckedConsumer<? super String>> implements CellConsumer {

	public static final int DEFAULT_MAX_NUMBER_OF_CELL_PER_ROW = 64 * 1024 * 1024;
	private final RC rowConsumer;
	private final CC commentConsumer;
	private final char separatorChar;
	private final char escapeChar;
	private final int maxNumberOfCellPerRow;

	private int currentIndex;
	private int currentLength = 8;

	private String[] currentRow = new String[currentLength];
	private StringBuilder commentBuilder = new StringBuilder();

	private boolean currentRowIsComment;

	private YamlCommentStringArrayConsumer(RC rowConsumer, CC commentConsumer, char separatorChar, char escapeChar, int maxNumberOfCellPerRow) {
		this.rowConsumer = rowConsumer;
		this.separatorChar = separatorChar;
		this.escapeChar = escapeChar;
		this.maxNumberOfCellPerRow = maxNumberOfCellPerRow;
		this.commentConsumer = commentConsumer;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (currentRowIsComment || (currentIndex == 0 && length > 0 && chars[offset] == '#')) {
			if (currentRowIsComment) {
				commentBuilder.append(separatorChar);
			} else {
				currentRowIsComment = true;
			}
			commentBuilder.append(chars, offset, length);
		} else {
			ensureCapacity();
			currentRow[currentIndex] = CellUtil.toUnescapedString(chars, offset, offset + length, escapeChar);
			currentIndex++;
		}
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
		} catch (Exception e) { return ErrorHelper.<Boolean>rethrow(e); }
	}

	private boolean _endOfRow() throws Exception {
		if (!currentRowIsComment) {
			rowConsumer.accept(Arrays.copyOf(currentRow, currentIndex));
			Arrays.fill(currentRow, 0, currentIndex, null);
			currentIndex = 0;
			return true;
		} else {
			commentConsumer.accept(commentBuilder.toString());
			commentBuilder.setLength(0);
			currentRowIsComment = false;
			return false;
		}
	}

	public RC rowConsumer() {
		return rowConsumer;
	}

	public CC commentConsumer() {
		return commentConsumer;
	}

	@Override
	public void end() {
		if (currentIndex > 0 || currentRowIsComment) {
			endOfRow();
		}
	}
	public static <RC extends CheckedConsumer<? super String[]>, CC extends CheckedConsumer<? super String>> YamlCommentStringArrayConsumer<RC, CC> newInstance(RC handler, CC commentConsumer, char separatorChar, char escapeChar, int maxNumberOfCellPerRow) {
		return new YamlCommentStringArrayConsumer<RC, CC>(handler, commentConsumer, separatorChar, escapeChar, maxNumberOfCellPerRow);
	}

	public static <RC extends CheckedConsumer<? super String[]>, CC extends CheckedConsumer<? super String>> YamlCommentStringArrayConsumer<RC, CC> newInstance(RC handler, CC commentConsumer, char separatorChar, char escapeChar) {
		return newInstance(handler, commentConsumer, separatorChar, escapeChar, DEFAULT_MAX_NUMBER_OF_CELL_PER_ROW);
	}

	public static void readWithComments(CsvParser.DSL dsl, Reader reader, CheckedConsumer<String[]> rowCollector, CheckedConsumer<String> commentCollector) throws IOException {
		YamlCommentStringArrayConsumer<CheckedConsumer<String[]>, CheckedConsumer<String>> cellConsumer = newInstance(rowCollector, commentCollector, dsl.separator(), dsl.quote());
		dsl.parse(reader, cellConsumer);
	}
}
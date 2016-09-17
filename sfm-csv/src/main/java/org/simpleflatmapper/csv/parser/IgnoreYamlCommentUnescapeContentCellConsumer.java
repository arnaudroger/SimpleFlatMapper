package org.simpleflatmapper.csv.parser;


public final class IgnoreYamlCommentUnescapeContentCellConsumer implements CellConsumer {

	private static final int COMMENT = 2;
	private static final int REGULAR_ROW = 1;
	private static final int NONE = 0;

	private final char escapeChar;
	private final CellConsumer delegate;

	private int state;


	public IgnoreYamlCommentUnescapeContentCellConsumer(char escapeChar, CellConsumer delegate) {
		this.escapeChar = escapeChar;
		this.delegate = delegate;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (state == NONE) {
			state = (length > 0 && chars[offset] == '#') ? COMMENT : REGULAR_ROW;
		}
		if (state == REGULAR_ROW) {
			if (length > 0  && chars[offset] == escapeChar) {
				int end = offset + length;
				offset ++;
				length = CellUtil.unescapeInPlace(chars, offset, end, escapeChar) - offset;
			}
			delegate.newCell(chars, offset, length);
		}
	}

	@Override
	public boolean endOfRow() {
		if (state != COMMENT) {
			state = NONE;
			return delegate.endOfRow();
		}
		state = NONE;
		return false;
	}

	@Override
	public void end() {
		delegate.end();
	}

}
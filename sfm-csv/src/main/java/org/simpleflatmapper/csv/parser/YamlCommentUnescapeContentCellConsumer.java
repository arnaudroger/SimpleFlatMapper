package org.simpleflatmapper.csv.parser;


import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class YamlCommentUnescapeContentCellConsumer implements CellConsumer {

	private static final int COMMENT = 2;
	private static final int REGULAR_ROW = 1;
	private static final int NONE = 0;

	private final char escapeChar;
	private final CellConsumer rowDelegate;
	private final CellConsumer commentDelegate;

	private int state;


	public YamlCommentUnescapeContentCellConsumer(char escapeChar,
                                                  CellConsumer rowDelegate,
												  CellConsumer commentDelegate) {
		this.escapeChar = escapeChar;
		this.rowDelegate = requireNonNull( "rowDelegate", rowDelegate);
		this.commentDelegate = requireNonNull( "commentDelegate", commentDelegate);
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (state == NONE) {
			state = (length > 0 && chars[offset] == '#') ? COMMENT : REGULAR_ROW;
		}
		if (state != COMMENT) {
			// unescape
			if (length > 0  && chars[offset] == escapeChar) {
				int end = offset + length;
				offset ++;
				length = CellUtil.unescapeInPlace(chars, offset, end, escapeChar) - offset;
			}
			rowDelegate.newCell(chars, offset, length);
		} else {
			commentDelegate.newCell(chars, offset, length);
		}
	}

	@Override
	public boolean endOfRow() {
		boolean b;
		if (state != COMMENT) {
			b =  rowDelegate.endOfRow();
		} else {
			b = commentDelegate.endOfRow();
		}
		state = NONE;
		return b;
	}

	@Override
	public void end() {
		rowDelegate.end();
		commentDelegate.end();
	}

}
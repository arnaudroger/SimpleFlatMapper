package org.simpleflatmapper.csv.parser;


import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class YamlCommentUnescapeContentCellConsumer implements CellConsumer {

	private static final int COMMENT = 2;
	private static final int REGULAR_ROW = 1;
	private static final int NONE = 0;

	private final CellConsumer rowDelegate;
	private final CellConsumer commentDelegate;
	private final CellPreProcessor rowCellPreProcessor;

	private int state;

	public YamlCommentUnescapeContentCellConsumer(CellPreProcessor rowCellPreProcessor,
                                                  CellConsumer rowDelegate,
												  CellConsumer commentDelegate) {
		this.rowCellPreProcessor = rowCellPreProcessor;
		this.rowDelegate = requireNonNull( "rowDelegate", rowDelegate);
		this.commentDelegate = requireNonNull( "commentDelegate", commentDelegate);
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (state == NONE) {
			state = (length > 0 && chars[offset] == '#') ? COMMENT : REGULAR_ROW;
		}
		if (state != COMMENT) {
			rowCellPreProcessor.newCell(chars, offset, offset + length, rowDelegate);
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
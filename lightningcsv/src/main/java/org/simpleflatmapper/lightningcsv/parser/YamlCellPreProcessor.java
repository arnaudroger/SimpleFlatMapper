package org.simpleflatmapper.lightningcsv.parser;


import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ErrorHelper;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class YamlCellPreProcessor extends CellPreProcessor {

	private static final int COMMENT = 2;
	private static final int REGULAR_ROW = 1;
	private static final int NONE = 0;

	private final boolean ignoreLeadingSpace;

	public YamlCellPreProcessor(boolean ignoreLeadingSpace) {
		this.ignoreLeadingSpace = ignoreLeadingSpace;
	}

	public void newCell(char[] chars, int start, int end, CellConsumer cellConsumer, int state) {
		YamlCellConsumer yamlCellConsumer = (YamlCellConsumer) cellConsumer;
		yamlCellConsumer.newCell(chars, start, end, state);
	}

	public boolean ignoreLeadingSpace() {
		return ignoreLeadingSpace;
	}

	public static CellConsumer commentConsumerToCellConsumer(CheckedConsumer<String> commentConsumer) {
		return commentConsumer != null ? new CommentConsumer(commentConsumer) : null;
	}

	public static final class CommentConsumer implements CellConsumer {

		private final CheckedConsumer<String> delegate;

		public CommentConsumer(CheckedConsumer<String> delegate) {
			this.delegate = delegate;
		}

		@Override
		public void newCell(char[] chars, int offset, int length) {
			try {
				delegate.accept(new String(chars, offset, length));
			} catch (Exception e) {
				ErrorHelper.rethrow(e);
			}
		}

		@Override
		public boolean endOfRow() {
			return true;
		}

		@Override
		public void end() {
		}
	}

	public static final class YamlCellConsumer implements CellConsumer {

		private final CellConsumer rowDelegate;
		private final CellConsumer commentDelegate;
		private final CellPreProcessor rowCellPreProcessor;


		private int rowState;

		public YamlCellConsumer(CellConsumer rowDelegate,
								CellConsumer commentDelegate,
								CellPreProcessor rowCellPreProcessor) {
			this.rowDelegate = requireNonNull( "rowDelegate", rowDelegate);
			this.commentDelegate = commentDelegate;
			this.rowCellPreProcessor = rowCellPreProcessor;
		}

		public void newCell(char[] chars, int offset, int length) {
			throw new UnsupportedOperationException();
		}

		public void newCell(char[] chars, int start, int end, int state) {
			if ((state &  ConfigurableCharConsumer.COMMENTED ) == 0) {
				rowCellPreProcessor.newCell(chars, start, end, rowDelegate, state);
				rowState = REGULAR_ROW;
			} else {
				if (commentDelegate != null) {
					commentDelegate.newCell(chars, start, end - start);
				}
				rowState = COMMENT;
			}
		}

		@Override
		public boolean endOfRow() {
			boolean b;
			if (rowState != COMMENT) {
				b = rowDelegate.endOfRow();
			} else {
				b = commentDelegate != null && commentDelegate.endOfRow();
			}
			rowState = NONE;
			return b;
		}

		@Override
		public void end() {
			rowDelegate.end();
			if (commentDelegate != null) commentDelegate.end();
		}
	}


}
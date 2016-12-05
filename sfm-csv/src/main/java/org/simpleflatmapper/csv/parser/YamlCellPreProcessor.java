package org.simpleflatmapper.csv.parser;


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


	public static final class YamlCellConsumer implements CellConsumer {

		public final CellConsumer rowDelegate;
		public final CellConsumer commentDelegate;
		public final CellPreProcessor rowCellPreProcessor;


		private int rowState;

		public YamlCellConsumer(CellConsumer rowDelegate,
								CellConsumer commentDelegate,
								CellPreProcessor rowCellPreProcessor) {
			this.rowDelegate = requireNonNull( "rowDelegate", rowDelegate);
			this.commentDelegate = requireNonNull( "commentDelegate", commentDelegate);
			this.rowCellPreProcessor = rowCellPreProcessor;
		}

		public void newCell(char[] chars, int offset, int length) {
			throw new UnsupportedOperationException();
		}

		public void newCell(char[] chars, int start, int end, int state) {

			if (rowState == NONE) {
				rowState = (end > start && chars[start] == '#') ? COMMENT : REGULAR_ROW;
			}
			if (rowState != COMMENT) {
				rowCellPreProcessor.newCell(chars, start, end, rowDelegate, state);
			} else {
				commentDelegate.newCell(chars, start, end - start);
			}
		}

		@Override
		public boolean endOfRow() {
			boolean b;
			if (rowState != COMMENT) {
				b =  rowDelegate.endOfRow();
			} else {
				b = commentDelegate.endOfRow();
			}
			rowState = NONE;
			return b;
		}

		@Override
		public void end() {
			rowDelegate.end();
			commentDelegate.end();
		}
	}


}
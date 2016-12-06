package org.simpleflatmapper.csv.parser;


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


	public static final class YamlCellConsumer implements CellConsumer {

		public final CellConsumer rowDelegate;
		public final CheckedConsumer<String> commentConsumer;
		public final CellPreProcessor rowCellPreProcessor;


		private int rowState;

		public YamlCellConsumer(CellConsumer rowDelegate,
								CheckedConsumer<String> commentConsumer,
								CellPreProcessor rowCellPreProcessor) {
			this.rowDelegate = requireNonNull( "rowDelegate", rowDelegate);
			this.commentConsumer =  commentConsumer;
			this.rowCellPreProcessor = rowCellPreProcessor;
		}

		public void newCell(char[] chars, int offset, int length) {
			throw new UnsupportedOperationException();
		}

		public void newCell(char[] chars, int start, int end, int state) {
			if ((state &  CharConsumer.COMMENTED ) == 0) {
				rowCellPreProcessor.newCell(chars, start, end, rowDelegate, state);
				rowState = REGULAR_ROW;
			} else {
				if (commentConsumer != null) {
					try {
						commentConsumer.accept(new String(chars, start, end - start));
					} catch (Exception e) {
						ErrorHelper.rethrow(e);
					}
				}
				rowState = COMMENT;
			}
		}

		@Override
		public boolean endOfRow() {
			boolean b;
			if (rowState != COMMENT) {
				b =  rowDelegate.endOfRow();
			} else {
				b = commentConsumer != null;
			}
			rowState = NONE;
			return b;
		}

		@Override
		public void end() {
			if (rowState == REGULAR_ROW) {
				endOfRow();
			}
			rowDelegate.end();
		}
	}


}
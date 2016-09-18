package org.simpleflatmapper.csv.parser;

import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ErrorHelper;

public final class StringConcatCellConsumer<RH extends CheckedConsumer<? super String>> implements CellConsumer {

	private final RH handler;
	private final char separatorChar;
	private final StringBuilder stringBuilder = new StringBuilder();
	private boolean hasData = false;

	private StringConcatCellConsumer(RH handler, char separatorChar) {
		this.handler = handler;
		this.separatorChar = separatorChar;
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (hasData) {
			stringBuilder.append(separatorChar);
		}
		stringBuilder.append(chars, offset, length);
		hasData = true;
	}


	@Override
	public boolean endOfRow() {
		try {
			return _endOfRow();
		} catch (Exception e) { return ErrorHelper.<Boolean>rethrow(e);  }
	}

	private boolean _endOfRow() throws Exception {
		if (hasData) {
			handler.accept(stringBuilder.toString());
			stringBuilder.setLength(0);
		}
		return true;
	}

	public RH handler() {
		return handler;
	}

	@Override
	public void end() {
	}

	public static <RH extends CheckedConsumer<? super String>> StringConcatCellConsumer<RH> newInstance(RH consumer, char separatorChar) {
		return new StringConcatCellConsumer<RH>(consumer, separatorChar);
	}
}
package org.sfm.csv.parser;

import java.util.function.Consumer;

//IFJAVA8_START
@FunctionalInterface
//IFJAVA8_END
public interface CellConsumer {
	void newCell(char[] chars, int offset, int length);
//IFJAVA8_START
	default
//IFJAVA8_END
	void endOfRow()
//IFJAVA8_START
	{
	}
//IFJAVA8_END
	;
//IFJAVA8_START
	default
//IFJAVA8_END
	void end()
//IFJAVA8_START
	{
	}
//IFJAVA8_END
	;


//IFJAVA8_START
	static CellConsumer of(Consumer<String> c) {
		return ((chars, offset, length) -> c.accept(new String(chars, offset, length)));
	}
//IFJAVA8_END
}

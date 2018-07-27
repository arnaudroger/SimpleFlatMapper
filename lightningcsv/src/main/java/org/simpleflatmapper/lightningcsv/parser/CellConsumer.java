package org.simpleflatmapper.lightningcsv.parser;

//IFJAVA8_START
import java.util.function.Consumer;
//IFJAVA8_END

//IFJAVA8_START
@FunctionalInterface
//IFJAVA8_END
public interface CellConsumer {
	void newCell(char[] chars, int offset, int length);
//IFJAVA8_START
	default
//IFJAVA8_END

	/**
	 * @return false if the row was skipped
	 */
	boolean endOfRow()
//IFJAVA8_START
	{
		return true;
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

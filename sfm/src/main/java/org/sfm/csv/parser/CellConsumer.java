package org.sfm.csv.parser;

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
}

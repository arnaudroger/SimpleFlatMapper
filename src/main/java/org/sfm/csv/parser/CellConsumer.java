package org.sfm.csv.parser;

public interface CellConsumer {
	void newCell(CharSequence value);
	void endOfRow();
	void end();
}

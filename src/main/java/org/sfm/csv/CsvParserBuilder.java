package org.sfm.csv;

import org.sfm.csv.parser.*;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.stream.Stream;

/**
* Created by e19224 on 08/12/2014.
*/
public final class CsvParserBuilder {
	private char separatorChar = ',';
	private char quoteChar= '"';
	private int bufferSize = 8192;

	public CsvParserBuilder separator(char c) {
		this.separatorChar = c;
        return this;
    }
	public CsvParserBuilder quote(char c) {
		this.quoteChar = c;
		return this;
	}

    public CsvParserBuilder bufferSize(int size) {
        this.bufferSize = size;
		return this;
    }

	public <CC extends CellConsumer> CC parseAll(Reader reader, CC cellConsumer) throws IOException {
		CsvReader csvreader = reader(reader);
		return csvreader.parseAll(cellConsumer);
	}

	public CsvReader reader(Reader reader) {
		return new CsvReader(reader, charConsumer());
	}

	private CsvCharConsumer charConsumer() {
		CharBuffer charBuffer = new CharBuffer(bufferSize);

		if (separatorChar == ',' && quoteChar == '"') {
			return new StandardCsvCharConsumer(charBuffer);
		} else {
			return new ConfigurableCsvCharConsumer(charBuffer, separatorChar, quoteChar);
		}

	}

	public Iterator<String[]> iterate(Reader reader) {
		return new CsvStringArrayIterator(reader(reader));
	}

	//IFJAVA8_START
	public Stream<String[]> stream(Reader r) {
		return CsvParser.stream(reader(r));
	}
	//IFJAVA8_END

}

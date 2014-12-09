package org.sfm.csv;

import org.sfm.csv.parser.*;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.stream.Stream;

public final class CsvParserBuilder {
	private char separatorChar = ',';
	private char quoteChar= '"';
	private int bufferSize = 8192;
	private int skip = 0;
	private int limit = -1;

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

	public CsvParserBuilder skip(int skip) {
		this.skip = skip;
		return this;
	}

	public CsvParserBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}

	public <CC extends CellConsumer> CC parse(Reader reader, CC cellConsumer) throws IOException {
		CsvReader csvreader = reader(reader);

		csvreader.skipRows(skip);

		if (limit == -1) {
			return csvreader.parseAll(cellConsumer);
		} else {
			return csvreader.parseRows(cellConsumer, limit);

		}
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

	public Iterator<String[]> iterate(Reader reader) throws IOException {
		CsvReader csvReader = reader(reader);
		csvReader.skipRows(skip);
		return new CsvStringArrayIterator(csvReader);
	}

	//IFJAVA8_START
	public Stream<String[]> stream(Reader reader) throws IOException {
		CsvReader csvReader = reader(reader);
		csvReader.skipRows(skip);
		return CsvParser.stream(csvReader);
	}
	//IFJAVA8_END

}

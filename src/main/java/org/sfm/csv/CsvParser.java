package org.sfm.csv;

import org.sfm.csv.parser.*;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public final class CsvParser {

	/**
	 *
	 * @param c the separator char
	 * @return the DSL object
	 */
	public static DSL separator(char c) {
		return schema().separator(c);
	}

	public static DSL bufferSize(int size) {
		return schema().bufferSize(size);
	}

	public static DSL quote(char c) {
		return schema().quote(c);
	}

	public static DSL skip(int skip) {
		return schema().skip(skip);
	}

	private static DSL schema() {
		return new DSL();
	}

	public static DSL limit(int limit) {
		return schema().limit(limit);
	}

	/**
	 * @param reader the reader
	 * @return a csv reader based on the default setup.
	 */
	public static CsvReader reader(Reader reader) throws IOException {
		return schema().reader(reader);
	}

	public static Iterator<String[]> iterate(Reader reader) throws IOException {
		return schema().iterate(reader);
	}

	public static <CC extends CellConsumer> CC parse(Reader reader, CC cellConsumer) throws IOException {
		return schema().parse(reader, cellConsumer);
	}

	//IFJAVA8_START
	public static Stream<String[]> stream(Reader r) throws IOException {
		return schema().stream(r);
	}
	//IFJAVA8_END

	public static final class DSL {
        private final char separatorChar;
        private final char quoteChar;
        private final int bufferSize;
        private final int skip;
        private final int limit;

		private DSL() {
			separatorChar = ',';
			quoteChar= '"';
			bufferSize = 8192;
			skip = 0;
			limit = -1;
		}

		public DSL(char separatorChar, char quoteChar, int bufferSize, int skip, int limit) {
			this.separatorChar = separatorChar;
			this.quoteChar = quoteChar;
			this.bufferSize = bufferSize;
			this.skip = skip;
			this.limit = limit;
		}

		/**
         * set the separator character. the default value is ','.
         * @param c the new separator character
         * @return this
         */
        public DSL separator(char c) {
			return new DSL(c, quoteChar, bufferSize, skip, limit);
        }

        /**
         * set the quote character. the default value is '"'.
         * @param c the quote character
         * @return this
         */
        public DSL quote(char c) {
			return new DSL(separatorChar, c, bufferSize, skip, limit);
        }

        /**
         * set the size of the char buffer to read from.
         * @param size the size in bytes
         * @return this
         */
        public DSL bufferSize(int size) {
			return new DSL(separatorChar, quoteChar, size, skip, limit);
        }

        /**
         * set the number of line to skip.
         * @param skip number of line to skip.
         * @return this
         */
        public DSL skip(int skip) {
			return new DSL(separatorChar, quoteChar, bufferSize, skip, limit);
        }

        /**
         * set the number of row to process.
         * @param limit number of row to process
         * @return this
         */
        public DSL limit(int limit) {
			return new DSL(separatorChar, quoteChar, bufferSize, skip, limit);
        }

        /**
         * Parse the content from the reader as a csv and call back the cellConsumer with the cell values.
         * @param reader the reader
         * @param cellConsumer the callback object for each cell value
         * @return cellConsumer
         * @throws java.io.IOException
         */
        public <CC extends CellConsumer> CC parse(Reader reader, CC cellConsumer) throws IOException {
            CsvReader csvreader = reader(reader);

            if (limit == -1) {
                return csvreader.parseAll(cellConsumer);
            } else {
                return csvreader.parseRows(cellConsumer, limit);

            }
        }

        /**
         * Create a CsvReader and the specified reader. Will skip the number of specified rows.
         * @param reader the content
         * @return a CsvReader on the reader.
         * @throws java.io.IOException
         */
        public CsvReader reader(Reader reader) throws IOException {
            CsvReader csvReader = new CsvReader(reader, charConsumer());
            csvReader.skipRows(skip);
            return csvReader;
        }

        public Iterator<String[]> iterate(Reader reader) throws IOException {
            return reader(reader).iterator();
        }

        //IFJAVA8_START
        public Stream<String[]> stream(Reader reader) throws IOException {
			return reader(reader).stream();
		}
        //IFJAVA8_END

        private CsvCharConsumer charConsumer() {
            CharBuffer charBuffer = new CharBuffer(bufferSize);

            if (separatorChar == ',' && quoteChar == '"') {
                return new StandardCsvCharConsumer(charBuffer);
            } else {
                return new ConfigurableCsvCharConsumer(charBuffer, separatorChar, quoteChar);
            }

        }


		public int bufferSize() {
			return bufferSize;
		}

		public int limit() {
			return limit;
		}

		public int skip() {
			return skip;
		}

		public char separator() {
			return separatorChar;
		}

		public char quote() {
			return quoteChar;
		}
	}
}

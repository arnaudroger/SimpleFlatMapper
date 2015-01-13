package org.sfm.csv;

import org.sfm.csv.impl.DynamicCsvMapper;
import org.sfm.csv.parser.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.tuples.*;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
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

	public static <T> MapToDSL<T> mapTo(Type type) {
		return schema().mapTo(type);
	}

	public static <T> MapToDSL<T> mapTo(Class<T> type) {
		return mapTo((Type)type);
	}

	public static <T1, T2> MapToDSL<Tuple2<T1, T2>> mapTo(Class<T1> class1, Class<T2> class2) {
		return  schema().mapTo(class1, class2);
	}

	public static <T1, T2, T3> MapToDSL<Tuple3<T1, T2, T3>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3) {
		return  schema().mapTo(class1, class2, class3);
	}

	public static <T1, T2, T3, T4> MapToDSL<Tuple4<T1, T2, T3, T4>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4) {
		return  schema().mapTo(class1, class2, class3, class4);
	}

	public static <T1, T2, T3, T4, T5> MapToDSL<Tuple5<T1, T2, T3, T4, T5>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5) {
		return  schema().mapTo(class1, class2, class3, class4, class5);
	}

	public static <T> MapWithDSL<T> mapWith(CsvMapper<T> mapper) {
		return schema().mapWith(mapper);
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

		public <T> MapToDSL<T> mapTo(Type target) {
			return new MapToDSL<T>(this, target);
		}

		public <T> MapToDSL<T> mapTo(Class<T> target) {
			return mapTo((Type)target);
		}

		public <T1, T2> MapToDSL<Tuple2<T1, T2>> mapTo(Class<T1> class1, Class<T2> class2) {
			return new MapToDSL<Tuple2<T1, T2>>(this, Tuples.typeDef(class1, class2));
		}

		public <T1, T2, T3> MapToDSL<Tuple3<T1, T2, T3>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3) {
			return new MapToDSL<Tuple3<T1, T2, T3>>(this, Tuples.typeDef(class1, class2, class3));
		}

		public <T1, T2, T3, T4> MapToDSL<Tuple4<T1, T2, T3, T4>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4) {
			return new MapToDSL<Tuple4<T1, T2, T3, T4>>(this, Tuples.typeDef(class1, class2, class3, class4));
		}

		public <T1, T2, T3, T4, T5> MapToDSL<Tuple5<T1, T2, T3, T4, T5>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5) {
			return new MapToDSL<Tuple5<T1, T2, T3, T4, T5>>(this, Tuples.typeDef(class1, class2, class3, class4, class5));
		}

		public <T> MapWithDSL<T> mapWith(CsvMapper<T> mapper) {
			return new MapWithDSL(this, mapper);
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


	public static final class MapToDSL<T> {
		private final DSL dsl;
		private final CsvMapper<T> mapper;
		private final ClassMeta<T> classMeta;
		private final Type mapToClass;

		public MapToDSL(DSL dsl, Type mapToClass) {
			this.dsl = dsl;
			this.mapToClass = mapToClass;
			this.classMeta = ReflectionService.classMeta(mapToClass);
			this.mapper = new DynamicCsvMapper<T>(mapToClass, classMeta);
		}

		public MapToDSL(DSL dsl, ClassMeta<T> classMeta, Type mapToClass, String[] headers) {
			this.dsl = dsl;
			this.classMeta = classMeta;
			this.mapToClass = mapToClass;
			this.mapper = newStaticMapper(classMeta, headers);
		}

		private CsvMapper<T> newStaticMapper(ClassMeta<T> classMeta, String[] headers) {
			CsvMapperBuilder builder = new CsvMapperBuilder(mapToClass, classMeta);
			for(String header : headers) {
				builder.addMapping(header);
			}
			return builder.mapper();
		}

		public MapToDSL<T> headers(String... headers) {
			return new MapToDSL<T>(dsl, classMeta, mapToClass, headers);
		}

		public Iterator<T> iterate(Reader reader) throws IOException {
			return mapper.iterate(dsl.reader(reader));
		}

		//IFJAVA8_START
		public Stream<T> stream(Reader reader) throws IOException {
			return mapper.stream(dsl.reader(reader));
		}
		//IFJAVA8_END
	}

	public static final class MapWithDSL<T> {
		private final DSL dsl;
		private final CsvMapper<T> mapper;

		public MapWithDSL(DSL dsl, CsvMapper<T> mapper) {
			this.dsl = dsl;
			this.mapper = mapper;
		}

		public Iterator<T> iterate(Reader reader) throws IOException {
			return mapper.iterate(dsl.reader(reader));
		}

		//IFJAVA8_START
		public Stream<T> stream(Reader reader) throws IOException {
			return mapper.stream(dsl.reader(reader));
		}
		//IFJAVA8_END
	}
}

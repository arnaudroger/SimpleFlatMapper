package org.simpleflatmapper.csv;

import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;
import org.simpleflatmapper.csv.impl.CsvColumnDefinitionProviderImpl;
import org.simpleflatmapper.csv.impl.DynamicCsvMapper;
import org.simpleflatmapper.csv.parser.*;
import org.simpleflatmapper.map.CaseInsensitiveFieldKeyNamePredicate;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuple3;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuple6;
import org.simpleflatmapper.tuple.Tuple7;
import org.simpleflatmapper.tuple.Tuple8;
import org.simpleflatmapper.tuple.Tuples;
import org.simpleflatmapper.util.ConstantUnaryFactory;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.CloseableIterator;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.CheckedConsumer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
//IFJAVA8_START
import java.util.function.Function;
import java.util.stream.Stream;
//IFJAVA8_END

/**
 * CsvParser provides an fluent DSL to parse or map csv content.<p>
 * It is possible to customize the quote char, the separator, skip lines,and specified the size of the buffer<br>
 * <br>
 * <code>
 *     CsvParser
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;.quote('\'')
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;.separator(';')
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;.skip(2)
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;.bufferSize(256)
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;.stream(new StringReader("1;1\n2;2\n3;3\n4;4\n5;5"))
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;.map(Arrays::toString)
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;.forEach(System.out::println);
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;// output
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;// [3, 3]
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;// [4, 4]
 *     <br>&nbsp;&nbsp;&nbsp;&nbsp;// [5, 5]
 * </code>
 * <br>
 * <br>
 *     the limit settings does not affect streams or iterator, only parse on DSL and forEach on the mapTo/mapWith DSL.
 * <p>
 * The DSL provides way to mapTo an object <br><br>
 * <code>
 *     CsvParser.mapTo(MyClass.class).stream(reader).forEach(System.out::println);<br>
 * </code>
 * <p>
 *  using static mapping when no headers<br><br>
 * <code>
 *     CsvParser.mapTo(MyClass.class).addHeaders("id", "field").stream(reader).forEach(System.out::println);<br>
 *     // using the addMapping<br>
 *     CsvParser.mapTo(MyClass.class).addMapping("id").addMapping("field").stream(reader).forEach(System.out::println);<br>
 * </code>
 * <p>
 *  using static mapping and ignoring source header<br><br>
 * <code>
 *     CsvParser.mapTo(MyClass.class).overrideHeaders("id", "field").stream(reader).forEach(System.out::println);<br>
 *     // using the addMapping<br>
 *     CsvParser.skip(1).mapTo(MyClass.class).addMapping("id").addMapping("field").stream(reader).forEach(System.out::println);<br>
 * </code>
 * <p>
 *  or mapping with a predefined jdbcMapper.<br><br>
 * <code>
 *     CsvMapper&lt;MyClass&gt; jdbcMapper = CsvMapperFactory.newInstance().newMapper(MyClass.class);<br>
 *     CsvParser.mapWith(jdbcMapper).stream(reader).forEach(System.out::println);<br>
 * </code>
 *
 * <p>
 *  Each call to the DSL return an immutable representation of the current setup. So that it is possible to cache step of the DSL without side effect.<br><br>
 * <code>
 *     CsvParser.DSL semiColumnParser = CsvParser.separator(';');<br>
 *     <br>
 *     try (Reader r = new FileReader(file)) {<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;// the limit does not modify to the semiColumnParser object<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;semiColumnParser.limit(3).stream(r);<br>
 *     }
 * </code>
 *
 *
 */
public final class CsvParser {
	public static final int DEFAULT_MAX_BUFFER_SIZE_8M = 1 << 23;
	public static final int DEFAULT_BUFFER_SIZE_4K = 1024 * 4;

	/**
	 *
	 * @param c the separator char
	 * @return the DSL object
	 */
	public static DSL separator(char c) {
		return dsl().separator(c);
	}

	public static DSL bufferSize(int size) {
		return dsl().bufferSize(size);
	}

	public static DSL maxBufferSize(int size) {
		return dsl().maxBufferSize(size);
	}

	public static DSL quote(char c) {
		return dsl().quote(c);
	}

	public static DSL skip(int skip) {
		return dsl().skip(skip);
	}

	public static DSL dsl() {
		return new DSL();
	}

	public static DSL limit(int limit) {
		return dsl().limit(limit);
	}

	public static <T> MapToDSL<T> mapTo(Type type) {
		return dsl().mapTo(type);
	}

	public static <T> MapToDSL<T> mapTo(Class<T> type) {
		return dsl().mapTo(type);
	}

    public static <T> MapToDSL<T> mapTo(TypeReference<T> type) {
        return dsl().mapTo(type);
    }

    public static <T1, T2> MapToDSL<Tuple2<T1, T2>> mapTo(Class<T1> class1, Class<T2> class2) {
		return  dsl().mapTo(class1, class2);
	}

	public static <T1, T2, T3> MapToDSL<Tuple3<T1, T2, T3>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3) {
		return  dsl().mapTo(class1, class2, class3);
	}

	public static <T1, T2, T3, T4> MapToDSL<Tuple4<T1, T2, T3, T4>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4) {
		return  dsl().mapTo(class1, class2, class3, class4);
	}

	public static <T1, T2, T3, T4, T5> MapToDSL<Tuple5<T1, T2, T3, T4, T5>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5) {
		return  dsl().mapTo(class1, class2, class3, class4, class5);
	}

    public static <T1, T2, T3, T4, T5, T6> MapToDSL<Tuple6<T1, T2, T3, T4, T5, T6>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5, Class<T6> class6) {
        return  dsl().mapTo(class1, class2, class3, class4, class5, class6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> MapToDSL<Tuple7<T1, T2, T3, T4, T5, T6, T7>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5, Class<T6> class6, Class<T7> class7) {
        return  dsl().mapTo(class1, class2, class3, class4, class5, class6, class7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> MapToDSL<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5, Class<T6> class6, Class<T7> class7, Class<T8> class8) {
        return  dsl().mapTo(class1, class2, class3, class4, class5, class6, class7, class8);
    }

    public static <T> MapWithDSL<T> mapWith(CsvMapper<T> mapper) {
		return dsl().mapWith(mapper);
	}

	/**
	 * @param reader the reader
	 * @return a csv reader based on the default setup.
     * @throws java.io.IOException if an error occurs reading the data
	 */
	public static CsvReader reader(Reader reader) throws IOException {
		return dsl().reader(reader);
	}

    public static CsvReader reader(CharSequence content) throws IOException {
        return dsl().reader(content);
    }

	public static CsvReader reader(String content) throws IOException {
		return dsl().reader(content);
	}

	public static CloseableCsvReader reader(File file) throws IOException {
		return dsl().reader(file);
	}

	public static Iterator<String[]> iterator(Reader reader) throws IOException {
		return dsl().iterator(reader);
	}

	public static Iterator<String[]> iterator(CharSequence content) throws IOException {
        return dsl().iterator(content);
    }

	public static CloseableIterator<String[]> iterator(File file) throws IOException {
		return dsl().iterator(file);
	}

	public static <H extends CheckedConsumer<String[]>> H forEach(Reader reader, H consumer) throws IOException {
		return dsl().forEach(reader, consumer);
	}

	public static <H extends CheckedConsumer<String[]>> H forEach(CharSequence content, H consumer) throws IOException {
		return dsl().forEach(content, consumer);
	}

	public static <H extends CheckedConsumer<String[]>> H forEach(File file, H consumer) throws IOException {
		return dsl().forEach(file, consumer);
	}

	public static <CC extends CellConsumer> CC parse(Reader reader, CC cellConsumer) throws IOException {
		return dsl().parse(reader, cellConsumer);
	}

	public static <CC extends CellConsumer> CC parse(CharSequence content, CC cellConsumer) throws IOException {
        return dsl().parse(content, cellConsumer);
    }

	public static <CC extends CellConsumer> CC parse(String content, CC cellConsumer) throws IOException {
		return dsl().parse(content, cellConsumer);
	}

	public static <CC extends CellConsumer> CC parse(File file, CC cellConsumer) throws IOException {
		return dsl().parse(file, cellConsumer);
	}

	//IFJAVA8_START
	public static Stream<String[]> stream(Reader r) throws IOException {
		return dsl().stream(r);
	}

	@Deprecated
	public static Stream<String[]> stream(File file) throws IOException {
		return dsl().stream(file);
	}

	public static <R> R stream(File file, Function<Stream<String[]>, R> function) throws IOException {
		return dsl().stream(file, function);
	}

	public static Stream<String[]> stream(String content) throws IOException {
        return dsl().stream(content);
    }
	//IFJAVA8_END

	protected static abstract class AbstractDSL<D extends AbstractDSL<D>> {
		protected final char separatorChar;
		protected final char quoteChar;
		protected final int bufferSize;
		protected final int skip;
		protected final int limit;
		protected final int maxBufferSize;
		protected final StringPostProcessing stringPostProcessing;
		protected final org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper;

		protected enum StringPostProcessing { NONE, UNESCAPE, TRIM_AND_UNESCAPE}

		protected AbstractDSL() {
			separatorChar = ',';
			quoteChar= '"';
			bufferSize = DEFAULT_BUFFER_SIZE_4K;
			skip = 0;
			limit = -1;
			maxBufferSize = DEFAULT_MAX_BUFFER_SIZE_8M;
			stringPostProcessing = StringPostProcessing.UNESCAPE;
			cellConsumerWrapper = null;
		}

		protected AbstractDSL(char separatorChar, char quoteChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper) {
			this.separatorChar = separatorChar;
			this.quoteChar = quoteChar;
			this.bufferSize = bufferSize;
			this.skip = skip;
			this.limit = limit;
			this.maxBufferSize = maxBufferSize;
			this.stringPostProcessing = stringPostProcessing;
			this.cellConsumerWrapper = cellConsumerWrapper;
		}

		/**
		 * Parse the content from the reader as a csv and call back the cellConsumer with the cell values.
		 * @param reader the reader
		 * @param cellConsumer the callback object for each cell value
		 * @param <CC> the type of the cell consumer
		 * @return cellConsumer
		 * @throws IOException if and error occurs in the reader
		 */
		public final <CC extends CellConsumer> CC parse(Reader reader, CC cellConsumer) throws IOException {
			return parse(charBuffer(reader), cellConsumer);
		}

		public final <CC extends CellConsumer> CC parse(String content, CC cellConsumer) throws IOException {
			return parse(charBuffer(content), cellConsumer);
		}

		public final <CC extends CellConsumer> CC parse(CharSequence content, CC cellConsumer) throws IOException {
			return parse(charBuffer(content), cellConsumer);
		}

		private <CC extends CellConsumer> CC parse(CharBuffer charBuffer, CC cellConsumer) throws IOException {
			CsvReader csvreader = reader(charBuffer);

			if (limit == -1) {
				return csvreader.parseAll(cellConsumer);
			} else {
				return csvreader.parseRows(cellConsumer, limit);
			}
		}

		public final <CC extends CellConsumer> CC parse(File file, CC cellConsumer) throws IOException {
			Reader reader = new FileReader(file);
			try {
				return parse(reader, cellConsumer);
			} finally {
				try { reader.close(); } catch(IOException e) { /* ignore*/ }
			}
		}

		/**
		 * Create a CsvReader and the specified reader. Will skip the number of specified rows.
		 * @param reader the content
		 * @return a CsvReader on the reader.
		 * @throws IOException if an io error occurs
		 */
		public final CsvReader reader(Reader reader) throws IOException {
			return reader(charBuffer(reader));
		}

		public final CsvReader reader(CharSequence content) throws IOException {
			return reader(charBuffer(content));
		}

		public final CsvReader reader(String content) throws IOException {
			return reader(charBuffer(content));
		}

		private CsvReader reader(CharBuffer charBuffer) throws IOException {
			CsvReader csvReader = new CsvReader(charConsumer(charBuffer), cellConsumerWrapper);
			csvReader.skipRows(skip);
			return csvReader;
		}

		protected CharBuffer charBuffer(Reader reader) throws IOException {
			return new ReaderCharBuffer(bufferSize, maxBufferSize, reader);
		}

		protected CharBuffer charBuffer(CharSequence content) throws IOException {
			return new CharSequenceCharBuffer(content);
		}

		protected CharBuffer charBuffer(String content) throws IOException {
			return new CharSequenceCharBuffer(content);
		}

		public final CloseableCsvReader reader(File file) throws IOException {
			return onReader(file, this, CREATE_CLOSEABLE_CSV_READER);
		}

		public final Iterator<String[]> iterator(Reader reader) throws IOException {
			return reader(reader).iterator();
		}

		public final Iterator<String[]> iterator(CharSequence content) throws IOException {
			return reader(content).iterator();
		}

		public final Iterator<String[]> iterator(String content) throws IOException {
			return reader(content).iterator();
		}

		public final CloseableIterator<String[]> iterator(File file) throws IOException {
			return onReader(file, this, CREATE_CLOSEABLE_ITERATOR);
		}

		public final <H extends CheckedConsumer<String[]>> H forEach(Reader reader, H consumer) throws IOException {
			return reader(reader).read(consumer);
		}

		public final <H extends CheckedConsumer<String[]>> H forEach(CharSequence content, H consumer) throws IOException {
			return reader(content).read(consumer);
		}

		public final <H extends CheckedConsumer<String[]>> H forEach(String content, H consumer) throws IOException {
			return reader(content).read(consumer);
		}

		public final <H extends CheckedConsumer<String[]>> H forEach(File file, H consumer) throws IOException {
			CloseableCsvReader csvReader = reader(file);
			try {
				csvReader.read(consumer);
			} finally {
				csvReader.close();
			}
			return consumer;
		}

		public final <T> MapToDSL<T> mapTo(Type target) {
			return new MapToDSL<T>(this, target);
		}

		public final <T> MapToDSL<T> mapTo(Class<T> target) {
			return mapTo((Type)target);
		}

		public final <T> MapToDSL<T> mapTo(TypeReference<T> target) {
			return mapTo(target.getType());
		}

		public final <T1, T2> MapToDSL<Tuple2<T1, T2>> mapTo(Class<T1> class1, Class<T2> class2) {
			return new MapToDSL<Tuple2<T1, T2>>(this, Tuples.typeDef(class1, class2));
		}

		public final <T1, T2, T3> MapToDSL<Tuple3<T1, T2, T3>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3) {
			return new MapToDSL<Tuple3<T1, T2, T3>>(this, Tuples.typeDef(class1, class2, class3));
		}

		public final <T1, T2, T3, T4> MapToDSL<Tuple4<T1, T2, T3, T4>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4) {
			return new MapToDSL<Tuple4<T1, T2, T3, T4>>(this, Tuples.typeDef(class1, class2, class3, class4));
		}

		public final <T1, T2, T3, T4, T5> MapToDSL<Tuple5<T1, T2, T3, T4, T5>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5) {
			return new MapToDSL<Tuple5<T1, T2, T3, T4, T5>>(this, Tuples.typeDef(class1, class2, class3, class4, class5));
		}

		public final <T1, T2, T3, T4, T5, T6> MapToDSL<Tuple6<T1, T2, T3, T4, T5, T6>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5, Class<T6> class6) {
			return new MapToDSL<Tuple6<T1, T2, T3, T4, T5, T6>>(this, Tuples.typeDef(class1, class2, class3, class4, class5, class6));
		}

		public final <T1, T2, T3, T4, T5, T6, T7> MapToDSL<Tuple7<T1, T2, T3, T4, T5, T6, T7>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5, Class<T6> class6, Class<T7> class7) {
			return new MapToDSL<Tuple7<T1, T2, T3, T4, T5, T6, T7>>(this, Tuples.typeDef(class1, class2, class3, class4, class5, class6, class7));
		}

		public final <T1, T2, T3, T4, T5, T6, T7, T8> MapToDSL<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> mapTo(Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4, Class<T5> class5, Class<T6> class6, Class<T7> class7, Class<T8> class8) {
			return new MapToDSL<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>(this, Tuples.typeDef(class1, class2, class3, class4, class5, class6, class7, class8));
		}

		public final <T> MapWithDSL<T> mapWith(CsvMapper<T> mapper) {
			return new MapWithDSL<T>(this, mapper);
		}

		//IFJAVA8_START
		public final Stream<String[]> stream(Reader reader) throws IOException {
			return reader(reader).stream();
		}

		public final Stream<String[]> stream(CharSequence content) throws IOException {
			return reader(content).stream();
		}

		public final Stream<String[]> stream(String content) throws IOException {
			return reader(content).stream();
		}
		//IFJAVA8_END
		/**
		 * Use @see AbstractDSL#stream(File, Function).
 		 * @param file the file
		 * @return a stream of String[]
		 */
		//IFJAVA8_START
		@Deprecated
		public final Stream<String[]> stream(File file) throws IOException {
			return onReader(file, this, (reader, dsl) -> dsl.stream(reader).onClose(() -> { try { reader.close(); } catch (IOException e) {} }));
		}

		public final <R> R stream(File file, Function<Stream<String[]>, R> function) throws IOException {
			Reader reader = new FileReader(file);
			try {
				return function.apply(stream(reader));
			} catch(IOException ioe) {
				try { reader.close(); } catch(IOException ioe2) { }
				throw ioe;
			}
		}
		//IFJAVA8_END

		protected final CharConsumer charConsumer(CharBuffer charBuffer) throws IOException {
			final TextFormat textFormat = getTextFormat();

			if (isCsv()) {
				return new CsvCharConsumer(charBuffer);
			} else {
				return new ConfigurableCharConsumer(charBuffer, textFormat, getCellTransformer(textFormat));
			}
		}

		private boolean isCsv() {
			return quoteChar == '"' && separatorChar == ',' && stringPostProcessing == StringPostProcessing.UNESCAPE;
		}

		private TextFormat getTextFormat() {
			return new TextFormat(separatorChar, quoteChar);
		}

		private CellTransformer getCellTransformer(TextFormat textFormat) {
			CellTransformer cellTransformer;
			switch (stringPostProcessing) {
				case TRIM_AND_UNESCAPE:
					cellTransformer = new TrimAndUnescapeCellTransformer(textFormat.getEscapeChar());
					break;
				case UNESCAPE:
					cellTransformer = new UnescapeCellTransformer(textFormat.getEscapeChar());
					break;
				case NONE:
					cellTransformer = new NoopCellTransformer();
					break;
				default:
					throw new IllegalStateException("Could not instantiate char consumer " + stringPostProcessing);
			}
			return cellTransformer;
		}

		public final int maxBufferSize() {
			return maxBufferSize;
		}

		public final int bufferSize() {
			return bufferSize;
		}

		public final int limit() {
			return limit;
		}

		public final int skip() {
			return skip;
		}

		public final char separator() {
			return separatorChar;
		}

		public final char quote() {
			return quoteChar;
		}


		/**
		 * set the separator character. the default value is ','.
		 * @param c the new separator character
		 * @return this
		 */
		public D separator(char c) {
			return newDSL(c, quoteChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}

		/**
		 * set the quote character. the default value is '"'.
		 * @param c the quote character
		 * @return this
		 */
		public D quote(char c) {
			return newDSL(separatorChar, c, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}

		/**
		 * set the size of the char buffer to read from.
		 * @param size the size in bytes
		 * @return this
		 */
		public D bufferSize(int size) {
			return newDSL(separatorChar, quoteChar, size, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}

		/**
		 * set the number of line to skip.
		 * @param skip number of line to skip.
		 * @return this
		 */
		public D skip(int skip) {
			return newDSL(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}

		/**
		 * set the number of row to process. limit does not affect stream or iterator.
		 * @param limit number of row to process
		 * @return this
		 */
		public D limit(int limit) {
			return newDSL(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}

		/**
		 * set the maximum size of the content the parser will handle before failing to avoid OOM.
		 * @param maxBufferSize the maximum size the buffer will grow, default 8M
		 * @return this
		 */
		public D maxBufferSize(int maxBufferSize) {
			return newDSL(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}


		protected abstract D newDSL(char separatorChar, char quoteChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper);


	}
    /**
     * DSL for csv parsing.
     * @see CsvParser
     */
	public static final class DSL extends AbstractDSL<DSL> {

		protected DSL() {
		}

		protected DSL(char separatorChar, char quoteChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper) {
			super(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}



		public DSL trimSpaces() {
            return new DSL(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, StringPostProcessing.TRIM_AND_UNESCAPE, cellConsumerWrapper);
        }

		public DSLYamlComment withYamlComments() {
			return new DSLYamlComment(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, StringPostProcessing.NONE,
					new org.simpleflatmapper.util.Function<CellConsumer, CellConsumer>() {
						@Override
						public CellConsumer apply(CellConsumer cellConsumer) {
							return new YamlCommentUnescapeContentCellConsumer(quoteChar, cellConsumer, IgnoreCellConsumer.INSTANCE);
						}
					}
			);

		}

		public DSL disableUnescaping() {
			return new DSL(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, StringPostProcessing.NONE, cellConsumerWrapper);
		}

		@Override
		protected DSL newDSL(char separatorChar, char quoteChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper) {
			return new DSL(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}

	}


    public static final class DSLYamlComment extends AbstractDSL<DSLYamlComment> {
		protected DSLYamlComment(char separatorChar, char quoteChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper) {
			super(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}

		private CsvReader rawReader(CharBuffer charBuffer) throws IOException {
			CsvReader csvReader = new CsvReader(charConsumer(charBuffer), null);
			csvReader.skipRows(skip);
			return csvReader;
		}

		public void forEach(Reader reader, CheckedConsumer<String[]> rowConsumer, CheckedConsumer<String> commentConsumer) throws IOException {
			_forEach(rawReader(charBuffer(reader)), rowConsumer, commentConsumer);
		}

		public void forEach(CharSequence content, CheckedConsumer<String[]> rowConsumer, CheckedConsumer<String> commentConsumer) throws IOException {
			_forEach(rawReader(charBuffer(content)), rowConsumer, commentConsumer);
		}

		public void forEach(String content, CheckedConsumer<String[]> rowConsumer, CheckedConsumer<String> commentConsumer) throws IOException {
			_forEach(rawReader(charBuffer(content)), rowConsumer, commentConsumer);
		}

		private void _forEach(CsvReader reader, CheckedConsumer<String[]> rowConsumer, CheckedConsumer<String> commentConsumer) throws IOException {
			reader.parseAll(newYamlCellConsumer(rowConsumer, commentConsumer));
		}

		private YamlCommentUnescapeContentCellConsumer newYamlCellConsumer(CheckedConsumer<String[]> rowConsumer, CheckedConsumer<String> commentConsumer) {
			return new YamlCommentUnescapeContentCellConsumer(quoteChar, StringArrayCellConsumer.newInstance(rowConsumer), StringConcatCellConsumer.newInstance(commentConsumer, separatorChar));
		}

		public void forEach(File file, CheckedConsumer<String[]> rowConsumer, CheckedConsumer<String> commentConsumer) throws IOException {
			CloseableCsvReader csvReader = rawReader(file);
			try {
				csvReader.parseAll(newYamlCellConsumer(rowConsumer, commentConsumer));
			} finally {
				csvReader.close();
			}
		}

		private final CloseableCsvReader rawReader(File file) throws IOException {
			return onReader(file, this, CREATE_CLOSEABLE_CSV_RAW_READER);
		}

		private static final OnReaderFactory<CloseableCsvReader, DSLYamlComment> CREATE_CLOSEABLE_CSV_RAW_READER =
				new OnReaderFactory<CloseableCsvReader, DSLYamlComment>() {
					@Override
					public CloseableCsvReader apply(Reader reader, DSLYamlComment dsl) throws IOException {
						return new CloseableCsvReader(dsl.rawReader(dsl.charBuffer(reader)), reader);
					}
				};



		@Override
		protected DSLYamlComment newDSL(char separatorChar, char quoteChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper) {
			return new DSLYamlComment(separatorChar, quoteChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper);
		}

	}
    /**
     * DSL for csv mapping to a dynamic jdbcMapper.
     * @see CsvParser
     * @see CsvMapper
     */
	public static final class MapToDSL<T> extends MapWithDSL<T> {
		private final ClassMeta<T> classMeta;
		private final Type mapToClass;
		private final CsvColumnDefinitionProviderImpl columnDefinitionProvider;

		public MapToDSL(AbstractDSL dsl, Type mapToClass) {
			this(dsl, ReflectionService.newInstance().<T>getClassMeta(mapToClass), mapToClass, new CsvColumnDefinitionProviderImpl());
		}
		private MapToDSL(AbstractDSL dsl, ClassMeta<T> classMeta, Type mapToClass, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
			super(dsl, new DynamicCsvMapper<T>(mapToClass, classMeta, columnDefinitionProvider));
			this.mapToClass = mapToClass;
			this.classMeta = classMeta;
			this.columnDefinitionProvider = columnDefinitionProvider;
		}

		public StaticMapToDSL<T> headers(String... headers) {
			return headers(headers, getDsl());

		}
		public StaticMapToDSL<T> overrideHeaders(String... headers) {
			return headers(headers, getDsl().skip(1));
		}

		private StaticMapToDSL<T> headers(String[] headers, AbstractDSL csvDsl) {
			return new StaticMapToDSL<T>(csvDsl, classMeta, mapToClass, getColumnDefinitions(headers), columnDefinitionProvider);
		}

		public StaticMapToDSL<T> defaultHeaders() {
			return defaultHeaders(getDsl());
		}

		public StaticMapToDSL<T> overrideWithDefaultHeaders() {
			return defaultHeaders(getDsl().skip(1));
		}

		private StaticMapToDSL<T> defaultHeaders(AbstractDSL csvDsl) {
			return new StaticMapToDSL<T>(
					csvDsl,
					classMeta,
					mapToClass,
					newDefaultStaticMapper(mapToClass, classMeta, columnDefinitionProvider),
					columnDefinitionProvider);
		}



		private List<Tuple2<String, CsvColumnDefinition>> getColumnDefinitions(String[] headers) {
			List<Tuple2<String,CsvColumnDefinition>> columns = new ArrayList<Tuple2<String, CsvColumnDefinition>>();
			for(String header : headers) {
				columns.add(new Tuple2<String, CsvColumnDefinition>(header, CsvColumnDefinition.identity()));
			}
			return columns;
		}

		public MapToDSL<T> columnDefinition(String column, CsvColumnDefinition columnDefinition) {
			return columnDefinition(new CaseInsensitiveFieldKeyNamePredicate(column), columnDefinition);
		}

		public MapToDSL<T> columnDefinition(Predicate<? super CsvColumnKey> predicate, CsvColumnDefinition columnDefinition) {
			return new MapToDSL<T>(getDsl(), classMeta, mapToClass, newColumnDefinitionProvider(predicate, columnDefinition));
		}

        public MapWithDSL<T> addKeys(String... keys) {
			List<AbstractColumnDefinitionProvider.PredicatedColunnPropertyFactory<CsvColumnDefinition, CsvColumnKey>> properties = columnDefinitionProvider.getProperties();

			for(String key : keys) {
                properties.add(new AbstractColumnDefinitionProvider.PredicatedColunnPropertyFactory<CsvColumnDefinition, CsvColumnKey>(
                		new CaseInsensitiveFieldKeyNamePredicate(key),
                        new ConstantUnaryFactory<CsvColumnKey, Object>(KeyProperty.DEFAULT)));
            }

            return new MapToDSL<T>(getDsl(), classMeta, mapToClass, new CsvColumnDefinitionProviderImpl(properties));
        }

        private CsvColumnDefinitionProviderImpl newColumnDefinitionProvider(Predicate<? super CsvColumnKey> predicate, CsvColumnDefinition columnDefinition) {
			List<AbstractColumnDefinitionProvider.PredicatedColunnPropertyFactory<CsvColumnDefinition, CsvColumnKey>> properties = columnDefinitionProvider.getProperties();
			for(Object property : columnDefinition.properties()) {
				properties.add(new AbstractColumnDefinitionProvider.PredicatedColunnPropertyFactory<CsvColumnDefinition, CsvColumnKey>(predicate, new ConstantUnaryFactory<CsvColumnKey, Object>(property)));
			}
			return new CsvColumnDefinitionProviderImpl(properties);
		}

		public StaticMapToDSL<T> addMapping(String column) {
			return staticMapper().addMapping(column);
		}

        public StaticMapToDSL<T> addKey(String key) {
            return staticMapper().addKey(key);
        }
		public StaticMapToDSL<T> addMapping(String column, CsvColumnDefinition columnDefinition) {
			return staticMapper().addMapping(column, columnDefinition);
		}

		private StaticMapToDSL<T> staticMapper() {
			return headers(new String[0], getDsl().skip(1));
		}


    }

    /**
     * DSL for csv mapping to a static jdbcMapper.
     * @see CsvParser
     * @see CsvMapper
     */
	public static final class StaticMapToDSL<T> extends  MapWithDSL<T> {
		private final ClassMeta<T> classMeta;
		private final Type mapToClass;
		private final CsvColumnDefinitionProviderImpl columnDefinitionProvider;
		private final List<Tuple2<String, CsvColumnDefinition>> columns;


		private StaticMapToDSL(AbstractDSL dsl, ClassMeta<T> classMeta, Type mapToClass,  CsvMapper<T> mapper, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
			super(dsl, mapper);
			this.classMeta = classMeta;
			this.mapToClass = mapToClass;
			this.columns = new ArrayList<Tuple2<String, CsvColumnDefinition>>();
			this.columnDefinitionProvider = columnDefinitionProvider;
		}

		private StaticMapToDSL(AbstractDSL dsl, ClassMeta<T> classMeta, Type mapToClass, List<Tuple2<String, CsvColumnDefinition>> columns, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
			super(dsl, newStaticMapper(mapToClass, classMeta, columns, columnDefinitionProvider));
			this.classMeta = classMeta;
			this.mapToClass = mapToClass;
			this.columns = columns;
			this.columnDefinitionProvider = columnDefinitionProvider;
		}

		public StaticMapToDSL<T> addMapping(String column) {
			return addMapping(column, CsvColumnDefinition.identity());
		}

		public StaticMapToDSL<T> addMapping(String column, CsvColumnDefinition columnDefinition) {
			List<Tuple2<String, CsvColumnDefinition>> newColumns = new ArrayList<Tuple2<String, CsvColumnDefinition>>(columns);
			newColumns.add(new Tuple2<String, CsvColumnDefinition>(column, columnDefinition));
			return new StaticMapToDSL<T>(getDsl(), classMeta, mapToClass, newColumns, columnDefinitionProvider);
		}

        public StaticMapToDSL<T> addKey(String key) {
            return addMapping(key, CsvColumnDefinition.key());
        }
    }

    /**
     * DSL for csv mapping to a provided jdbcMapper.
     * @see CsvParser
     * @see CsvMapper
     */
    public static class MapWithDSL<T> {
		private final AbstractDSL<?> dsl;
		private final CsvMapper<T> mapper;

		public MapWithDSL(AbstractDSL dsl, CsvMapper<T> mapper) {
			this.dsl = dsl;
			this.mapper = mapper;
		}

        protected final AbstractDSL getDsl() {
            return dsl;
        }

		public final Iterator<T> iterator(Reader reader) throws IOException {
			return mapper.iterator(dsl.reader(reader));
		}

		public final Iterator<T> iterator(CharSequence content) throws IOException {
			return mapper.iterator(dsl.reader(content));
		}

		public final Iterator<T> iterator(String content) throws IOException {
			return mapper.iterator(dsl.reader(content));
		}

		public final CloseableIterator<T> iterator(File file) throws IOException {
			OnReaderFactory<CloseableIterator<T>, AbstractDSL<?>> factory =
					new OnReaderFactory<CloseableIterator<T>, AbstractDSL<?>>() {
						@Override
						public CloseableIterator<T> apply(Reader reader, AbstractDSL<?> dsl) throws IOException {
							return new CloseableIterator<T>(iterator(reader), reader);
						}
					};
			return onReader(file, dsl, factory);
		}

		public final <H extends CheckedConsumer<T>> H forEach(File file, H consumer) throws IOException {
			Reader reader = new FileReader(file);
			try {
				return forEach(reader, consumer);
			} finally {
				try { reader.close(); } catch (IOException e) { }
			}
		}

		public final <H extends CheckedConsumer<T>> H forEach(Reader reader, H consumer) throws IOException {
			return forEach(consumer, dsl.reader(reader));
        }

		public final <H extends CheckedConsumer<T>> H forEach(CharSequence content, H consumer) throws IOException {
			return forEach(consumer, dsl.reader(content));
		}

		public final <H extends CheckedConsumer<T>> H forEach(String content, H consumer) throws IOException {
			return forEach(consumer, dsl.reader(content));
		}

		private <H extends CheckedConsumer<T>> H forEach(H consumer, CsvReader csvReader) throws IOException {
			if (dsl.limit == -1) {
                mapper.forEach(csvReader, consumer);
            } else {
                mapper.forEach(csvReader, consumer, dsl.limit);
            }
			return consumer;
		}

		//IFJAVA8_START
		public final Stream<T> stream(Reader reader) throws IOException {
			return mapper.stream(dsl.reader(reader));
		}

		public final Stream<T> stream(CharSequence content) throws IOException {
			return mapper.stream(dsl.reader(content));
		}

		public final Stream<T> stream(String content) throws IOException {
			return mapper.stream(dsl.reader(content));
		}
		//IFJAVA8_END
		/**
		 * use @see MapWithDSL#stream(File, Function)
		 * @param file the file
		 * @return a stream of T
		 */
		//IFJAVA8_START
		@Deprecated
		public final Stream<T> stream(File file) throws IOException {
			OnReaderFactory<Stream<T>, AbstractDSL<?>> factory =
					new OnReaderFactory<Stream<T>, AbstractDSL<?>>() {
						@Override
						public Stream<T> apply(Reader reader, AbstractDSL<?> dsl) throws IOException {
							return stream(reader).onClose(() -> {
								try {
									reader.close();
								} catch (IOException e) {
									// ignore
								}
							});
						}
					};
			return onReader(file, dsl, factory);
		}

		public final <R> R stream(File file, Function<Stream<T>, R> function) throws IOException {
			Reader reader = new FileReader(file);
			try {
				return function.apply(stream(reader));
			} catch(IOException ioe) {
				try { reader.close(); } catch(IOException ioe2) {  }
				throw ioe;
			}
		}
		//IFJAVA8_END
	}


	private static final OnReaderFactory<CloseableCsvReader, AbstractDSL<?>> CREATE_CLOSEABLE_CSV_READER =
			new OnReaderFactory<CloseableCsvReader, AbstractDSL<?>>() {
				@Override
				public CloseableCsvReader apply(Reader reader, AbstractDSL<?> dsl) throws IOException {
					return new CloseableCsvReader(dsl.reader(reader), reader);
				}
			};
	private static final OnReaderFactory<CloseableIterator<String[]>, AbstractDSL<?>> CREATE_CLOSEABLE_ITERATOR =
			new OnReaderFactory<CloseableIterator<String[]>, AbstractDSL<?>>() {
				@Override
				public CloseableIterator<String[]> apply(Reader reader, AbstractDSL<?> dsl) throws IOException {
					return new CloseableIterator<String[]>(dsl.iterator(reader), reader);
				}
			};

	interface OnReaderFactory<T, D extends AbstractDSL<?>> {
		T apply(Reader reader, D dsl) throws IOException;
	}

	protected static <R, D extends AbstractDSL<?>> R onReader(File file, D dsl, OnReaderFactory<R, ? super D> factory) throws IOException {
		Reader reader = new FileReader(file);
		try {
			return factory.apply(reader, dsl);
		} catch(IOException ioe) {
			try { reader.close(); } catch(IOException ioe2) { /* ignore*/ }
			throw ioe;
		}
	}

	private static <T> CsvMapper<T> newDefaultStaticMapper(Type mapToClass, ClassMeta<T> classMeta, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(mapToClass, classMeta, columnDefinitionProvider);
		builder.addDefaultHeaders();
		return builder.mapper();
	}
	private static <T> CsvMapper<T> newStaticMapper(Type mapToClass, ClassMeta<T> classMeta, List<Tuple2<String, CsvColumnDefinition>> columns, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(mapToClass, classMeta, columnDefinitionProvider);
		for(Tuple2<String, CsvColumnDefinition> col: columns) {
			builder.addMapping(col.first(), col.second());
		}
		return builder.mapper();
	}


}

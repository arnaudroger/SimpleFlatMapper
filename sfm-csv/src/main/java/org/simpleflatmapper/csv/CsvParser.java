package org.simpleflatmapper.csv;

import org.simpleflatmapper.csv.impl.CsvColumnDefinitionProviderImpl;
import org.simpleflatmapper.lightningcsv.CloseableCsvReader;
import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.lightningcsv.CsvParser.OnReaderFactory;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.lightningcsv.parser.CellPreProcessor;
import org.simpleflatmapper.lightningcsv.parser.CharBuffer;
import org.simpleflatmapper.lightningcsv.parser.StringArrayCellConsumer;
import org.simpleflatmapper.lightningcsv.parser.TextFormat;
import org.simpleflatmapper.lightningcsv.parser.YamlCellPreProcessor;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.map.property.RenameProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuple3;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuple6;
import org.simpleflatmapper.tuple.Tuple7;
import org.simpleflatmapper.tuple.Tuple8;
import org.simpleflatmapper.tuple.Tuples;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.CloseableIterator;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.CheckedConsumer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import static org.simpleflatmapper.lightningcsv.CsvParser.onReader;

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
	public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

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

	private static Reader newReader(File file) throws IOException {
		return newReader(file, DEFAULT_CHARSET);
	}
	private static Reader newReader(File file, Charset charset) throws IOException {
		//IFJAVA8_START
		if (true) {
			FileChannel fileChannel = FileChannel.open(file.toPath());
			try {
				return Channels.newReader(fileChannel, charset.newDecoder(), -1);
			} catch(Throwable e) {
				safeClose(fileChannel);
				throw e;
			}
		}
		//IFJAVA8_END
		
		return newReaderJava6(file, charset);
	}

	private static void safeClose(Closeable closeable) {
		if (closeable == null) return;
		try {
			closeable.close();
		} catch (IOException e) {
			// ignore
		}
	}

	private static Reader newReaderJava6(File file, Charset charset) throws IOException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		FileChannel fileChannel = null;
		
		try {
			fileChannel = randomAccessFile.getChannel();
			return Channels.newReader(fileChannel, charset.newDecoder(), -1);
		} catch(RuntimeException t) {
			safeClose(fileChannel);
			safeClose(randomAccessFile);
			throw t;
		} catch(Error t) {
			safeClose(fileChannel);
			safeClose(randomAccessFile);
			throw t;
		}
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

	protected static abstract class AbstractDSL<D extends AbstractDSL<D>> extends org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL<D> {

		public AbstractDSL() {
		}

		public AbstractDSL(char separatorChar, char quoteChar, char escapeChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper, boolean yamlComment, boolean parallelReader, boolean specialisedCharConsumer) {
			super(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
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

	}
    /**
     * DSL for csv parsing.
     * @see CsvParser
     */
	public static final class DSL extends AbstractDSL<DSL> {

		protected DSL() {
		}

		protected DSL(char separatorChar, char quoteChar, char escapeChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper, boolean yamlComment, boolean parallelReader, boolean specialisedCharConsumer) {
			super(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}



		public DSL trimSpaces() {
            return new DSL(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, StringPostProcessing.TRIM_AND_UNESCAPE, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
        }

		/**
		 * will parse line starting with # as yaml comment.
		 * comments line will be ignored unless using the special foreach call.
		 * @return this
		 */
		public DSLYamlComment withYamlComments() {
			return new DSLYamlComment(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing,
					new org.simpleflatmapper.util.Function<CellConsumer, CellConsumer>() {
						@Override
						public CellConsumer apply(CellConsumer cellConsumer) {
							TextFormat textFormat = getTextFormat();
							return new YamlCellPreProcessor.YamlCellConsumer(cellConsumer, null, getCellTransformer(textFormat, stringPostProcessing));
						}
					},
					true, parallelReader, specialisedCharConsumer);
		}

		/**
		 * will parse line starting with # as yaml comment.
		 * comments line will be come as a row of 1 cell.
		 * @return this
		 */
		public DSLYamlComment withYamlCommentsAsCell() {
			return new DSLYamlComment(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing,
					new org.simpleflatmapper.util.Function<CellConsumer, CellConsumer>() {
						@Override
						public CellConsumer apply(CellConsumer cellConsumer) {
							TextFormat textFormat = getTextFormat();
							return new YamlCellPreProcessor.YamlCellConsumer(cellConsumer, cellConsumer, getCellTransformer(textFormat, stringPostProcessing));
						}
					},
					true, parallelReader, specialisedCharConsumer);
		}

		public DSL disableUnescaping() {
			return new DSL(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, StringPostProcessing.NONE, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}

		@Override
		protected DSL newDSL(char separatorChar, char quoteChar, char escapeChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper, boolean yamlComment, boolean parallelReader, boolean specialisedCharConsumer) {
			return new DSL(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}

	}


    public static final class DSLYamlComment extends AbstractDSL<DSLYamlComment> {

		protected DSLYamlComment(char separatorChar, char quoteChar, char escapeChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper, boolean yamlComment, boolean parallelReader, boolean specialisedCharConsumer) {
			super(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
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

		private YamlCellPreProcessor.YamlCellConsumer newYamlCellConsumer(CheckedConsumer<String[]> rowConsumer, CheckedConsumer<String> commentConsumer) {
			TextFormat textFormat = getTextFormat();
			return new YamlCellPreProcessor.YamlCellConsumer(
					StringArrayCellConsumer.newInstance(rowConsumer),
					YamlCellPreProcessor.commentConsumerToCellConsumer(commentConsumer),
					superGetCellTransformer(textFormat, stringPostProcessing));
		}

		private CellPreProcessor superGetCellTransformer(TextFormat textFormat, StringPostProcessing stringPostProcessing) {
			return super.getCellTransformer(textFormat, stringPostProcessing);
		}

		@Override
		protected CellPreProcessor getCellTransformer(TextFormat textFormat, StringPostProcessing stringPostProcessing) {
			return new YamlCellPreProcessor(stringPostProcessing == StringPostProcessing.TRIM_AND_UNESCAPE);
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
		protected DSLYamlComment newDSL(char separatorChar, char quoteChar, char escapeChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper, boolean yamlComment, boolean parallelReader, boolean specialisedCharConsumer) {
			return new DSLYamlComment(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
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

		public MapToDSL(org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL dsl, Type mapToClass) {
			this(dsl, ReflectionService.newInstance().<T>getClassMeta(mapToClass), mapToClass, new CsvColumnDefinitionProviderImpl());
		}
		private MapToDSL(org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL dsl, ClassMeta<T> classMeta, Type mapToClass, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
			super(dsl, CsvMapperFactory.newInstance(columnDefinitionProvider).newMapper(classMeta));
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

		private StaticMapToDSL<T> headers(String[] headers, org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL csvDsl) {
			return new StaticMapToDSL<T>(csvDsl, classMeta, mapToClass, getColumnDefinitions(headers), columnDefinitionProvider);
		}

		/**
		 * the name can be misleading. What it will do is assume the headers from the property of the mapped object.
		 * As there is no guarantee on the order those will be found it's an unstable way of doing things and should not be used.
		 * You should specify the headers manually using headers(...).
		 */
		@Deprecated
		public StaticMapToDSL<T> defaultHeaders() {
			return defaultHeaders(getDsl());
		}

		/**
		 * the name can be misleading. What it will do is assume the headers from the property of the mapped object.
		 * As there is no guarantee on the order those will be found it's an unstable way of doing things and should not be used.
		 * You should specify the headers manually using overrideHeaders(...).
		 */
		@Deprecated
		public StaticMapToDSL<T> overrideWithDefaultHeaders() {
			return defaultHeaders(getDsl().skip(1));
		}

		private StaticMapToDSL<T> defaultHeaders(org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL csvDsl) {
			return new StaticMapToDSL<T>(
					csvDsl,
					classMeta,
					mapToClass,
					newDefaultStaticMapper(classMeta, columnDefinitionProvider),
					columnDefinitionProvider);
		}



		private List<Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>> getColumnDefinitions(String[] headers) {
			List<Tuple2<String,ColumnDefinition<CsvColumnKey, ?>>> columns = new ArrayList<Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>>();
			for(String header : headers) {
				columns.add(new Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>(header, FieldMapperColumnDefinition.<CsvColumnKey>identity()));
			}
			return columns;
		}

		public MapToDSL<T> columnDefinition(String column, ColumnDefinition<CsvColumnKey, ?> columnDefinition) {
			return new MapToDSL<T>(getDsl(), classMeta, mapToClass, newColumnDefinitionProvider(column, columnDefinition));
		}

		public MapToDSL<T> columnDefinition(Predicate<? super CsvColumnKey> predicate, ColumnDefinition<CsvColumnKey, ?> columnDefinition) {
			return new MapToDSL<T>(getDsl(), classMeta, mapToClass, newColumnDefinitionProvider(predicate, columnDefinition));
		}
		
		public MapToDSL<T> columnProperty(String column, Object property) {
			return new MapToDSL<T>(getDsl(), classMeta, mapToClass, newColumnDefinitionProviderWithProperty(column, property));
		}

		public MapToDSL<T> columnProperty(Predicate<? super CsvColumnKey> predicate, Object property) {
			return new MapToDSL<T>(getDsl(), classMeta, mapToClass, newColumnDefinitionProviderWithProperty(predicate, property));
		}
		
		public MapToDSL<T> alias(String column, String property) {
			return columnProperty(column, new RenameProperty(property));
		}

		public MapWithDSL<T> addKeys(String... keys) {
			CsvColumnDefinitionProviderImpl newProvider = (CsvColumnDefinitionProviderImpl) columnDefinitionProvider.copy();

			for(String key : keys) {
				newProvider.addColumnProperty(key, KeyProperty.DEFAULT);
            }

            return new MapToDSL<T>(getDsl(), classMeta, mapToClass, newProvider);
        }

		private CsvColumnDefinitionProviderImpl newColumnDefinitionProvider(String name, ColumnDefinition<CsvColumnKey, ?> columnDefinition) {
			CsvColumnDefinitionProviderImpl newProvider = (CsvColumnDefinitionProviderImpl) columnDefinitionProvider.copy();
			newProvider.addColumnDefinition(name, columnDefinition);
			return newProvider;
		}

		private CsvColumnDefinitionProviderImpl newColumnDefinitionProviderWithProperty(String name, Object property) {
			CsvColumnDefinitionProviderImpl newProvider = (CsvColumnDefinitionProviderImpl) columnDefinitionProvider.copy();
			newProvider.addColumnProperty(name, property);
			return newProvider;
		}

		private CsvColumnDefinitionProviderImpl newColumnDefinitionProviderWithProperty(Predicate<? super CsvColumnKey> predicate, Object property) {
			CsvColumnDefinitionProviderImpl newProvider = (CsvColumnDefinitionProviderImpl) columnDefinitionProvider.copy();
			newProvider.addColumnProperty(predicate, property);
			return newProvider;
		}

        private CsvColumnDefinitionProviderImpl newColumnDefinitionProvider(Predicate<? super CsvColumnKey> predicate, ColumnDefinition<CsvColumnKey, ?> columnDefinition) {
			CsvColumnDefinitionProviderImpl newProvider = (CsvColumnDefinitionProviderImpl) columnDefinitionProvider.copy();
			newProvider.addColumnDefinition(predicate, columnDefinition);
			return newProvider;
		}

		public StaticMapToDSL<T> addMapping(String column) {
			return staticMapper().addMapping(column);
		}

        public StaticMapToDSL<T> addKey(String key) {
            return staticMapper().addKey(key);
        }
		public StaticMapToDSL<T> addMapping(String column, FieldMapperColumnDefinition<CsvColumnKey> columnDefinition) {
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
		private final List<Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>> columns;


		private StaticMapToDSL(org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL dsl, ClassMeta<T> classMeta, Type mapToClass,  CsvMapper<T> mapper, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
			super(dsl, mapper);
			this.classMeta = classMeta;
			this.mapToClass = mapToClass;
			this.columns = new ArrayList<Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>>();
			this.columnDefinitionProvider = columnDefinitionProvider;
		}

		private StaticMapToDSL(org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL dsl, ClassMeta<T> classMeta, Type mapToClass, List<Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>> columns, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
			super(dsl, newStaticMapper(classMeta, columns, columnDefinitionProvider));
			this.classMeta = classMeta;
			this.mapToClass = mapToClass;
			this.columns = columns;
			this.columnDefinitionProvider = columnDefinitionProvider;
		}

		public StaticMapToDSL<T> addMapping(String column) {
			return addMapping(column, FieldMapperColumnDefinition.<CsvColumnKey>identity());
		}

		public StaticMapToDSL<T> addMapping(String column, ColumnDefinition<CsvColumnKey, ?> columnDefinition) {
			List<Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>> newColumns = new ArrayList<Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>>(columns);
			newColumns.add(new Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>(column, columnDefinition));
			return new StaticMapToDSL<T>(getDsl(), classMeta, mapToClass, newColumns, columnDefinitionProvider);
		}

        public StaticMapToDSL<T> addKey(String key) {
            return addMapping(key, FieldMapperColumnDefinition.<CsvColumnKey>key());
        }
    }

    /**
     * DSL for csv mapping to a provided jdbcMapper.
     * @see CsvParser
     * @see CsvMapper
     */
    public static class MapWithDSL<T> {
		private final org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL<?> dsl;
		private final CsvMapper<T> mapper;

		public MapWithDSL(org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL dsl, CsvMapper<T> mapper) {
			if (mapper == null) throw new NullPointerException();
			this.dsl = dsl;
			this.mapper = mapper;
		}

        protected final org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL getDsl() {
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
			OnReaderFactory<CloseableIterator<T>, org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL<?>> factory =
					new OnReaderFactory<CloseableIterator<T>, org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL<?>>() {
						@Override
						public CloseableIterator<T> apply(Reader reader, org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL<?> dsl) throws IOException {
							return new CloseableIterator<T>(iterator(reader), reader);
						}
					};
			return onReader(file, dsl, factory);
		}

		public final <H extends CheckedConsumer<T>> H forEach(File file, H consumer) throws IOException {
			Reader reader = newReader(file);
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
			if (dsl.limit() == -1) {
                mapper.forEach(csvReader, consumer);
            } else {
                mapper.forEach(csvReader, consumer, dsl.limit());
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
			OnReaderFactory<Stream<T>, org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL<?>> factory =
					new OnReaderFactory<Stream<T>, org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL<?>>() {
						@Override
						public Stream<T> apply(Reader reader, org.simpleflatmapper.lightningcsv.CsvParser.AbstractDSL<?> dsl) throws IOException {
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
			Reader reader = newReader(file);
			try {
				return function.apply(stream(reader));
			} catch(IOException ioe) {
				try { reader.close(); } catch(IOException ioe2) {  }
				throw ioe;
			}
		}
		//IFJAVA8_END
	}
	
	private static <T> CsvMapper<T> newDefaultStaticMapper(ClassMeta<T> classMeta, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(classMeta, columnDefinitionProvider);

		builder.addDefaultHeaders();
		return builder.mapper();
	}

	private static <T> CsvMapper<T> newStaticMapper(ClassMeta<T> classMeta, List<Tuple2<String, ColumnDefinition<CsvColumnKey, ?>>> columns, CsvColumnDefinitionProviderImpl columnDefinitionProvider) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(classMeta, columnDefinitionProvider);
		for(Tuple2<String, ColumnDefinition<CsvColumnKey, ?>> col: columns) {
			builder.addMapping(col.first(), col.second());
		}
		return builder.mapper();
	}


}

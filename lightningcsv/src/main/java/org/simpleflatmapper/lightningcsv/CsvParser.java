package org.simpleflatmapper.lightningcsv;

import org.simpleflatmapper.lightningcsv.parser.CharConsumerFactory;
import org.simpleflatmapper.lightningcsv.parser.AbstractCharConsumer;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.lightningcsv.parser.CellPreProcessor;
import org.simpleflatmapper.lightningcsv.parser.CharBuffer;
import org.simpleflatmapper.lightningcsv.parser.CharSequenceCharBuffer;
import org.simpleflatmapper.lightningcsv.parser.NoopCellPreProcessor;
import org.simpleflatmapper.lightningcsv.parser.ReaderCharBuffer;
import org.simpleflatmapper.lightningcsv.parser.StringArrayCellConsumer;
import org.simpleflatmapper.lightningcsv.parser.TextFormat;
import org.simpleflatmapper.lightningcsv.parser.TrimCellPreProcessor;
import org.simpleflatmapper.lightningcsv.parser.UnescapeCellPreProcessor;
import org.simpleflatmapper.lightningcsv.parser.YamlCellPreProcessor;
import org.simpleflatmapper.util.ParallelReader;
import org.simpleflatmapper.util.CloseableIterator;
import org.simpleflatmapper.util.CheckedConsumer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
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
	public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

	
	private static final CharConsumerFactory CHAR_CONSUMER_FACTORY = CharConsumerFactory.newInstance();
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

	public static abstract class AbstractDSL<D extends AbstractDSL<D>> {
		protected final char separatorChar;
		protected final char quoteChar;
		protected final char escapeChar;
		protected final int bufferSize;
		protected final int skip;
		protected final int limit;
		protected final int maxBufferSize;
		protected final StringPostProcessing stringPostProcessing;
		protected final org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper;
		protected final boolean yamlComment;
		protected final boolean parallelReader;
		protected final boolean specialisedCharConsumer;
		
		protected enum StringPostProcessing { NONE, UNESCAPE, TRIM_AND_UNESCAPE}

		protected AbstractDSL() {
			separatorChar = ',';
			quoteChar= '"';
			escapeChar = '"';
			bufferSize = DEFAULT_BUFFER_SIZE_4K;
			skip = 0;
			limit = -1;
			maxBufferSize = DEFAULT_MAX_BUFFER_SIZE_8M;
			stringPostProcessing = StringPostProcessing.UNESCAPE;
			cellConsumerWrapper = null;
			yamlComment = false;
			parallelReader = false;
			specialisedCharConsumer = true;
		}

		protected AbstractDSL(char separatorChar, char quoteChar, char escapeChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper, boolean yamlComment, boolean parallelReader, boolean specialisedCharConsumer) {
			this.separatorChar = separatorChar;
			this.quoteChar = quoteChar;
			this.escapeChar = escapeChar;
			this.bufferSize = bufferSize;
			this.skip = skip;
			this.limit = limit;
			this.maxBufferSize = maxBufferSize;
			this.stringPostProcessing = stringPostProcessing;
			this.cellConsumerWrapper = cellConsumerWrapper;
			this.yamlComment = yamlComment;
			this.parallelReader = parallelReader;
			this.specialisedCharConsumer = specialisedCharConsumer;
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
			Reader reader = newReader(file);
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
			return reader(charBuffer(parallelReader ? new ParallelReader(reader) : reader));
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

		public final Iterator<Row> rowIterator(Reader reader) throws IOException {
			return reader(reader).rowIterator();
		}

		public final Iterator<Row> rowIterator(CharSequence content) throws IOException {
			return reader(content).rowIterator();
		}

		public final Iterator<Row> rowIterator(String content) throws IOException {
			return reader(content).rowIterator();
		}

		public final CloseableIterator<Row> rowIterator(File file) throws IOException {
			return onReader(file, this, CREATE_CLOSEABLE_ROW_ITERATOR);
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

		public final Stream<Row> rowStream(Reader reader) throws IOException {
			return reader(reader).rowStream();
		}

		public final Stream<Row> rowStream(CharSequence content) throws IOException {
			return reader(content).rowStream();
		}

		public final Stream<Row> rowStream(String content) throws IOException {
			return reader(content).rowStream();
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
			Reader reader = newReader(file);
			try {
				return function.apply(stream(reader));
			} catch(IOException ioe) {
				try { reader.close(); } catch(IOException ioe2) { }
				throw ioe;
			}
		}

		public final <R> R rowStream(File file, Function<Stream<Row>, R> function) throws IOException {
			Reader reader = newReader(file);
			try {
				return function.apply(rowStream(reader));
			} catch(IOException ioe) {
				try { reader.close(); } catch(IOException ioe2) { }
				throw ioe;
			}
		}
		//IFJAVA8_END

		protected final AbstractCharConsumer charConsumer(CharBuffer charBuffer) {
			final TextFormat textFormat = getTextFormat();
			CellPreProcessor cellTransformer = getCellTransformer(textFormat, stringPostProcessing);
			
			return CHAR_CONSUMER_FACTORY.newCharConsumer(textFormat, charBuffer, cellTransformer, specialisedCharConsumer);
		}


		protected TextFormat getTextFormat() {
			return new TextFormat(separatorChar, quoteChar, escapeChar, yamlComment);
		}

		protected CellPreProcessor getCellTransformer(TextFormat textFormat, StringPostProcessing stringPostProcessing) {
			switch (stringPostProcessing) {
				case TRIM_AND_UNESCAPE:
					return new TrimCellPreProcessor(getUnescapeCellTransformer(textFormat));
				case UNESCAPE:
					return getUnescapeCellTransformer(textFormat);
				case NONE:
					return NoopCellPreProcessor.INSTANCE;
				default:
					throw new IllegalStateException("Could not instantiate char consumer " + stringPostProcessing);
			}
		}

		protected CellPreProcessor getUnescapeCellTransformer(TextFormat textFormat) {
			return new UnescapeCellPreProcessor(textFormat.escapeChar, textFormat.quoteChar);
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
			return newDSL(c, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}

		/**
		 * set the quote character. the default value is '"'.
		 * @param c the quote character
		 * @return this
		 */
		public D quote(char c) {
			return newDSL(separatorChar, c, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}

		/**
		 * set the quote character. the default value is '"'.
		 * @param c the quote character
		 * @return this
		 */
		public D escape(char c) {
			return newDSL(separatorChar, quoteChar, c, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}

		/**
		 * set the size of the char buffer to read from.
		 * @param size the size in bytes
		 * @return this
		 */
		public D bufferSize(int size) {
			return newDSL(separatorChar, quoteChar, escapeChar, size, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}

		/**
		 * set the number of line to skip.
		 * @param skip number of line to skip.
		 * @return this
		 */
		public D skip(int skip) {
			return newDSL(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}

		/**
		 * set the number of row to process. limit does not affect stream or iterator.
		 * @param limit number of row to process
		 * @return this
		 */
		public D limit(int limit) {
			return newDSL(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}


		/**
		 * on parsing from a reader the Reader will be fetched from a another thread. Use only if you have spare cores and the file is big enough.
		 * <p>
		 * On java 8 and over it will use the ForkJoinPool by default if the number of core is greater than 1, otherwise it will create a new Thread every time.
		 * </p><p>
		 * On java 6 and 7 it will create a shared ExecutorService with the number of threads set to the number of cores if it is greater than 1, otherwise it will create a new Thread every time.
		 * </p><p>
		 * If you wish to customize the org.simpleflatmapper.util.ParallelReader further for example to specify which Executor to user or the size of the ring buffer or read buffer you will need to wrap the Reader manually.
		 * </p>
		 * @return this
		 */
		public D parallelReader() {
			return newDSL(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, true, specialisedCharConsumer);
		}
		
		/**
		 * deactivate the parallelReader.
		 * @return this
		 */
		public D serialReader() {
			return newDSL(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, false, specialisedCharConsumer);
		}
		
		public D disableSpecialisedCharConsumer() {
			return newDSL(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, false);
		}
		
		/**
		 * set the maximum size of the content the parser will handle before failing to avoid OOM.
		 * @param maxBufferSize the maximum size the buffer will grow, default 8M
		 * @return this
		 */
		public D maxBufferSize(int maxBufferSize) {
			return newDSL(separatorChar, quoteChar, escapeChar, bufferSize, skip, limit, maxBufferSize, stringPostProcessing, cellConsumerWrapper, yamlComment, parallelReader, specialisedCharConsumer);
		}


		protected abstract D newDSL(char separatorChar, char quoteChar, char escapeChar, int bufferSize, int skip, int limit, int maxBufferSize, StringPostProcessing stringPostProcessing, org.simpleflatmapper.util.Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper, boolean yamlComment, boolean parallelReader, boolean specialisedCharConsumer);


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

	private static final OnReaderFactory<CloseableIterator<Row>, AbstractDSL<?>> CREATE_CLOSEABLE_ROW_ITERATOR =
			new OnReaderFactory<CloseableIterator<Row>, AbstractDSL<?>>() {
				@Override
				public CloseableIterator<Row> apply(Reader reader, AbstractDSL<?> dsl) throws IOException {
					return new CloseableIterator<Row>(dsl.rowIterator(reader), reader);
				}
			};

	public interface OnReaderFactory<T, D extends AbstractDSL<?>> {
		T apply(Reader reader, D dsl) throws IOException;
	}

	public static <R, D extends AbstractDSL<?>> R onReader(File file, D dsl, OnReaderFactory<R, ? super D> factory) throws IOException {
		Reader reader = newReader(file);
		try {
			return factory.apply(reader, dsl);
		} catch(IOException ioe) {
			try { reader.close(); } catch(IOException ioe2) { /* ignore*/ }
			throw ioe;
		}
	}



}

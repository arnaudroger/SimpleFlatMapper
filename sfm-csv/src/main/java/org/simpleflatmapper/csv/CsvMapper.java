package org.simpleflatmapper.csv;

import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.util.CheckedConsumer;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

/**
 * A CsvMapper will map from a {@link CsvReader} to an object of the specified type T.
 * <p>
 * There are 2 ways to instantiate a CsvMapper
 * <p>
 * Using {@link CsvMapperFactory}<br><br>
 * <code>
 *     CsvMapper jdbcMapper = CsvMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.newMapper(MyClass.class);<br>
 *     try (FileReader reader : new FileReader(file)) {<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;jdbcMapper.stream(reader).forEach(System.out::println);<br>
 *     }
 * </code>
 * <p>
 * Or Using the {@link CsvParser} DSL<br><br>
 * <code>
 *     try (FileReader reader : new FileReader(file)) {<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;CsvParser.mapTo(MyClass.class).stream(reader).forEach(System.out::println);<br>
 *     }
 * </code>
 * <p>
 * The CsvMapper can read from an {@link java.io.Reader} or
 * a {@link CsvReader} to gain more control on the csv parsing.<br>
 * You can instantiate a CsvReader using the {@link CsvParser} DSL<br>
 * <br>
 * <code>
 *     try (FileReader reader : new FileReader(file)) {<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;CsvReader csvReader = CsvParser.quote('\'').separator(';').skip(2).limit(5).reader(reader);<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;jdbcMapper.stream(csvReader).forEach(System.out::println);<br>
 *     }
 * </code>
 *
 *
 * @param <T> the type of the object the it will map to
 * @see CsvParser
 * @see CsvMapperFactory
 */
public interface CsvMapper<T> extends SetRowMapper<CsvRow, CsvRowSet, T, IOException> {

	/**
	 * Will map each row of the content of reader to an object of type T and will pass that object to the handle via the {@link CheckedConsumer}.handler(T t) call back.
	 * <p>
     * The method will return the handler passed as an argument so you can easily chain the calls like <br>
     * <code>
     *     List&lt;T&gt; list = jdbcMapper.forEach(reader, new ListHandler&lt;T&gt;()).getList();
     * </code>
     * <br>
	 *
	 * @param reader the reader
	 * @param handle the callback newInstance
     * @param <H> the row handler type
	 * @return the callback newInstance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle) throws IOException, MappingException;


	/**
	 * Will map each row of the content of reader to an object of type T and will pass that object to the handle via the {@link CheckedConsumer}.handler(T t) call back.
	 *
	 *
	 * @param reader the reader
	 * @param handle the callback newInstance
     * @param <H> the row handler type
	 * @return the callback newInstance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends CheckedConsumer<? super T>> H forEach(CsvReader reader, H handle) throws IOException, MappingException;


	/**
	 * Will map each row of the content of reader, starting at rowStart, to an object of type T and will pass that object to the handle via the {@link CheckedConsumer}.handler(T t) call back.
	 *
	 *
	 * @param reader the reader
	 * @param handle the callback newInstance
	 * @param skip the number of row to skip
     * @param <H> the row handler type
	 * @return the callback newInstance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle, int skip) throws IOException, MappingException;


	/**
	 * Will map each row of the content of reader, starting at rowStart and ending before rowEnd, to an object of type T and will pass that object to the handle via the {@link CheckedConsumer}.handler(T t) call back.
	 *
	 *
	 * @param reader the reader
	 * @param handle the callback newInstance
	 * @param skip the number of row to skip
	 * @param limit the number of row to process
     * @param <H> the row handler type
	 * @return the callback newInstance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle, int skip, int limit) throws IOException, MappingException;

	/**
	 * Will map each row of the content of reader, starting at rowStart and ending before rowEnd, to an object of type T and will pass that object to the handle via the {@link CheckedConsumer}.handler(T t) call back.
	 *
	 *
	 * @param reader the reader
	 * @param handle the callback newInstance
	 * @param limit the number of row to process
     * @param <H> the row handler type
	 * @return the callback newInstance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends CheckedConsumer<? super T>> H forEach(CsvReader reader, H handle, int limit) throws IOException, MappingException;

	/**
	 * Will return an iterator on the reader that will return a mapped object for each row.
	 *
	 * @param reader the reader
	 * @return an iterator on the file
     * @throws IOException if an io error occurs
	 */
	Iterator<T> iterator(Reader reader) throws IOException;

	/**
	 * Will return an iterator on the reader that will return a mapped object for each row.
	 *
	 * @param reader the reader
	 * @return an iterator on the file
     * @throws IOException if an io error occurs
	 */
	Iterator<T> iterator(CsvReader reader) throws IOException;

	/**
	 * Will return an iterator on the reader that will return a mapped object for each row.
	 *
	 * @param reader the reader
	 * @param skip the number of row to skip
	 * @return an iterator on the file
     * @throws IOException if an io error occurs
	 */
	Iterator<T> iterator(Reader reader, int skip) throws IOException;

	/**
	 * Will return a Stream of T
	 *
	 * @param reader the reader
	 * @return stream of T
     * @throws IOException if an io error occurs
	 */
	//IFJAVA8_START
	Stream<T> stream(Reader reader) throws IOException;
	//IFJAVA8_END
	/**
	 * Will return a Stream of T
	 *
	 * @param reader the reader
	 * @return stream of T
     * @throws IOException if an io error occurs
	 */
	//IFJAVA8_START
	Stream<T> stream(CsvReader reader) throws IOException;
	//IFJAVA8_END

	/**
	 * Will return a Stream of T.
	 *
	 * @param reader the reader
	 * @param skip the number of row to skip
	 * @return stream of T
     * @throws IOException if an io error occurs
	 */
	//IFJAVA8_START
	Stream<T> stream(Reader reader, int skip) throws IOException;
	//IFJAVA8_END

}

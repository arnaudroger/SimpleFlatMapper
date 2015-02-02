package org.sfm.csv;

import org.sfm.map.MappingException;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public interface CsvMapper<T> {
	
	/**
	 * Will map each row of the content of reader to an object of type T and will pass that object to the handle via the {@link RowHandler}.handler(T t) call back.
	 * 
	 * 
	 * @param reader the reader
	 * @param handle the callback instance
	 * @return the callback instance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends RowHandler<T>> H forEach(Reader reader, H handle) throws IOException, MappingException;


	/**
	 * Will map each row of the content of reader to an object of type T and will pass that object to the handle via the {@link RowHandler}.handler(T t) call back.
	 *
	 *
	 * @param reader the reader
	 * @param handle the callback instance
	 * @return the callback instance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends RowHandler<T>> H forEach(CsvReader reader, H handle) throws IOException, MappingException;


	/**
	 * Will map each row of the content of reader, starting at rowStart, to an object of type T and will pass that object to the handle via the {@link RowHandler}.handler(T t) call back.
	 * 
	 * 
	 * @param reader the reader
	 * @param handle the callback instance
	 * @param skip the number of row to skip
	 * @return the callback instance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends RowHandler<T>> H forEach(Reader reader, H handle, int skip) throws IOException, MappingException;


	/**
	 * Will map each row of the content of reader, starting at rowStart and ending before rowEnd, to an object of type T and will pass that object to the handle via the {@link RowHandler}.handler(T t) call back.
	 * 
	 * 
	 * @param reader the reader
	 * @param handle the callback instance
	 * @param skip the number of row to skip
	 * @param limit the number of row to process
	 * @return the callback instance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends RowHandler<T>> H forEach(Reader reader, H handle, int skip, int limit) throws IOException, MappingException;

	/**
	 * Will map each row of the content of reader, starting at rowStart and ending before rowEnd, to an object of type T and will pass that object to the handle via the {@link RowHandler}.handler(T t) call back.
	 *
	 *
	 * @param reader the reader
	 * @param handle the callback instance
	 * @param skip the number of row to skip
	 * @param limit the number of row to process
	 * @return the callback instance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends RowHandler<T>> H forEach(CsvReader reader, H handle, int limit) throws IOException, MappingException;

	/**
	 * Will return an iterator on the reader that will return a mapped object for each row.
	 * 
	 * @param reader the reader
	 * @return an iterator on the file
	 * @throws IOException
	 */
	Iterator<T> iterator(Reader reader) throws IOException;

	@Deprecated
	Iterator<T> iterate(Reader reader) throws IOException;

	/**
	 * Will return an iterator on the reader that will return a mapped object for each row.
	 *
	 * @param reader the reader
	 * @return an iterator on the file
	 * @throws IOException
	 */
	Iterator<T> iterator(CsvReader reader) throws IOException;

	@Deprecated
	Iterator<T> iterate(CsvReader reader) throws IOException;

	/**
	 * Will return an iterator on the reader that will return a mapped object for each row.
	 * 
	 * @param reader the reader
	 * @param skip the number of row to skip
	 * @return an iterator on the file
	 * @throws IOException
	 */
	Iterator<T> iterator(Reader reader, int skip) throws IOException;

	@Deprecated
	Iterator<T> iterate(Reader reader, int skip) throws IOException;

	/**
	 * Will return a Stream of T
	 * 
	 * @param reader the reader
	 * @return stream of T
	 * @throws IOException
	 */
	//IFJAVA8_START
	Stream<T> stream(Reader reader) throws IOException;
	//IFJAVA8_END
	/**
	 * Will return a Stream of T
	 *
	 * @param reader the reader
	 * @return stream of T
	 * @throws IOException
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
	 * @throws IOException
	 */
	//IFJAVA8_START
	Stream<T> stream(Reader reader, int skip) throws IOException;
	//IFJAVA8_END

}

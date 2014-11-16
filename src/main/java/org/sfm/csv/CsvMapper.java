package org.sfm.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

import org.sfm.map.MappingException;
import org.sfm.utils.RowHandler;

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
	 * Will map each row of the content of reader, starting at rowStart, to an object of type T and will pass that object to the handle via the {@link RowHandler}.handler(T t) call back.
	 * 
	 * 
	 * @param reader the reader
	 * @param handle the callback instance
	 * @param rowStart the row at which we start mapping, starts at 0
	 * @return the callback instance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends RowHandler<T>> H forEach(Reader reader, H handle, int rowStart) throws IOException, MappingException;
	
	/**
	 * Will map each row of the content of reader, starting at rowStart and ending before rowEnd, to an object of type T and will pass that object to the handle via the {@link RowHandler}.handler(T t) call back.
	 * 
	 * 
	 * @param reader the reader
	 * @param handle the callback instance
	 * @param rowStart the row at which we start mapping, starts at 0
	 * @param rowEnd the row before the last row we will map, starts at 0
	 * @return the callback instance
	 * @throws IOException if an io error occurs
	 * @throws MappingException if an mapping error occurs
	 */
	<H extends RowHandler<T>> H forEach(Reader reader, H handle, int rowStart, int rowEnd) throws IOException, MappingException;

	/**
	 * Will return an iterator on the reader that will return a mapped object for each row.
	 * 
	 * @param reader the reader
	 * @return an iterator on the file
	 * @throws IOException
	 */
	Iterator<T> iterate(Reader reader) throws IOException;

	/**
	 * Will return an iterator on the reader that will return a mapped object for each row.
	 * 
	 * @param reader the reader
	 * @param rowStart the row at which we start mapping, starts at 0
	 * @return an iterator on the file
	 * @throws IOException
	 */
	Iterator<T> iterate(Reader reader, int rowStart) throws IOException;

	//IFJAVA8_START
	/**
	 * Will return a Stream of T
	 * 
	 * @param reader the reader
	 * @return stream of T
	 * @throws IOException
	 */
	Stream<T> stream(Reader reader) throws IOException;
	
	/**
	 * Will return a Stream of T.
	 * 
	 * @param reader the reader
	 * @return stream of T
	 * @throws IOException
	 */
	Stream<T> stream(Reader reader, int rowStart) throws IOException;
	//IFJAVA8_END
}

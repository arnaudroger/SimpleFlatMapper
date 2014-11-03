package org.sfm.csv;

import java.io.IOException;
import java.io.Reader;

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

}

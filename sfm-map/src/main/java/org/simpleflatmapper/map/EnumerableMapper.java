package org.simpleflatmapper.map;

import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Enumerable;

import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

/**
 *
 * @param <SET> Enumerable Source type
 * @param <T> Target type
 * @param <E> Source exception type
 */
public interface EnumerableMapper<SET, T, E extends Exception> {

    /**
     * Loop over the resultSet, map each row to a new newInstance of T and call back the handler
     *<p>
     * The method will return the handler passed as an argument so you can easily chain the calls like <br>
     * <code>
     *     List&lt;T&gt; list = jdbcMapper.forEach(rs, new ListHandler&lt;T&gt;()).getList();
     * </code>
     * <br>
     *
     * @param source the source
     * @param handler the handler that will get the callback
     * @param <H> the row handler type
     * @return the handler passed in
     * @throws E if source error occurs
     * @throws MappingException if an error occurs during the mapping
     *
     */
    <H extends CheckedConsumer<? super T>> H forEach(final SET source, final H handler)
            throws E, MappingException;
    /**
     *
     * @param source the source
     * @return an iterator that will return a map object for each row of the result set.
     * @throws E if source error occurs
     * @throws MappingException if an error occurs during the mapping
     */
    Iterator<T> iterator(SET source)
            throws E, MappingException;
    
    /**
     *
     * @param source the source
     * @return a stream that will contain a map object for each row of the result set.
     * @throws E if source error occurs
     * @throws MappingException if an error occurs during the mapping
     */
    //IFJAVA8_START
    Stream<T> stream(SET source) throws E, MappingException;
    //IFJAVA8_END

    Enumerable<T> enumerate(SET source)
            throws E, MappingException;
    

}

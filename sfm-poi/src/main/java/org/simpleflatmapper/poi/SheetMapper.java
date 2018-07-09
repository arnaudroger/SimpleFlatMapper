package org.simpleflatmapper.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Enumerable;

import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public interface SheetMapper<T> extends EnumerableMapper<Sheet, T, RuntimeException> {

    /**
     *
     * @param sheet the sheet to map from
     * @return an iterator of mapped newInstance of T
     */
    @Override
    Iterator<T> iterator(Sheet sheet);

    /**
     *
     * @param startRow row index to start at
     * @param sheet the sheet to map from
     * @return an iterator of mapped newInstance of T
     */
    Iterator<T> iterator(int startRow, Sheet sheet);

    /**
     *
     * @param sheet the sheet to map from
     * @return an iterator of mapped newInstance of T
     */
    @Override
    Enumerable<T> enumerate(Sheet sheet);

    /**
     *
     * @param startRow row index to start at
     * @param sheet the sheet to map from
     * @return an iterator of mapped newInstance of T
     */
    Enumerable<T> enumerate(int startRow, Sheet sheet);
    
    /**
     *
     * @param sheet the sheet to map from
     * @param consumer the handler to call back
     * @param <RH> the type of the handler
     * @return the handler
     */
    @Override
    <RH extends CheckedConsumer<? super T>> RH forEach(Sheet sheet, RH consumer);

    /**
     *
     * @param startRow row index to start at
     * @param sheet the sheet to map from
     * @param consumer the handler to call back
     * @param <RH> the type of the handler
     * @return the handler
     */
    <RH extends CheckedConsumer<? super T>> RH forEach(int startRow, Sheet sheet, RH consumer);


    /**
     *
     * @param sheet the sheet to map from
     * @return a stream on mapped newInstance of T
     */
    //IFJAVA8_START
    @Override
    Stream<T> stream(Sheet sheet);
    //IFJAVA8_END

    /**
     *
     * @param startRow row index to start at
     * @param sheet the sheet to map from
     * @return a stream on mapped newInstance of T
     */
    //IFJAVA8_START
    Stream<T> stream(int startRow, Sheet sheet);
    //IFJAVA8_END
}

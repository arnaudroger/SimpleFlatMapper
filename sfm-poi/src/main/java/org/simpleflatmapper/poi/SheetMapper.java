package org.simpleflatmapper.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.util.RowHandler;

import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public interface SheetMapper<T> {

    /**
     *
     * @param sheet the sheet to map from
     * @return an iterator of mapped newInstance of T
     */
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
     * @param rowHandler the handler to call back
     * @param <RH> the type of the handler
     * @return the handler
     */
    <RH extends RowHandler<T>> RH forEach(Sheet sheet, RH rowHandler);

    /**
     *
     * @param startRow row index to start at
     * @param sheet the sheet to map from
     * @param rowHandler the handler to call back
     * @param <RH> the type of the handler
     * @return the handler
     */
    <RH extends RowHandler<T>> RH forEach(int startRow, Sheet sheet, RH rowHandler);


    /**
     *
     * @param sheet the sheet to map from
     * @return a stream on mapped newInstance of T
     */
    //IFJAVA8_START
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

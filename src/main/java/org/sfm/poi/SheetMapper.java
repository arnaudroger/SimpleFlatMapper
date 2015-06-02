package org.sfm.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.sfm.utils.RowHandler;

import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public interface SheetMapper<T> {

    /**
     *
     * @param sheet the sheet to map from
     * @return an iterator of mapped instance of T
     */
    Iterator<T> iterator(Sheet sheet);

    /**
     *
     * @param startRow row index to start at
     * @param sheet the sheet to map from
     * @return an iterator of mapped instance of T
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

    //IFJAVA8_START
    /**
     *
     * @param sheet the sheet to map from
     * @return a stream on mapped instance of T
     */
    Stream<T> stream(Sheet sheet);

    /**
     *
     * @param startRow row index to start at
     * @param sheet the sheet to map from
     * @return a stream on mapped instance of T
     */
    Stream<T> stream(int startRow, Sheet sheet);
    //IFJAVA8_END
}

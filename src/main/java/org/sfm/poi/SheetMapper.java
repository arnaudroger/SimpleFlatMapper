package org.sfm.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.sfm.utils.RowHandler;

import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public interface SheetMapper<T> {
    Iterator<T> iterator(Sheet sheet);

    Iterator<T> iterator(int startRow, Sheet sheet);

    <RH extends RowHandler<T>> RH forEach(Sheet sheet, RH rowHandler);

    <RH extends RowHandler<T>> RH forEach(int startRow, Sheet sheet, RH rowHandler);

    //IFJAVA8_START
    Stream<T> stream(Sheet sheet);

    Stream<T> stream(int startRow, Sheet sheet);
    //IFJAVA8_END
}

package org.simpleflatmapper.poi;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.SetRowMapper;

public interface RowMapper<T> extends SheetMapper<T>, SetRowMapper<Row, Sheet, T, RuntimeException> {
}

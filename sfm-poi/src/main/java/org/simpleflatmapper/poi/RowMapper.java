package org.simpleflatmapper.poi;


import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.map.Mapper;

public interface RowMapper<T> extends SheetMapper<T>, Mapper<Row, T> {
}

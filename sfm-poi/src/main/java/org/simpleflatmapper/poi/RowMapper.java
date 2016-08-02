package org.simpleflatmapper.poi;


import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.core.map.Mapper;

public interface RowMapper<T> extends SheetMapper<T>, Mapper<Row, T> {
}

package org.sfm.poi;


import org.apache.poi.ss.usermodel.Row;
import org.sfm.map.Mapper;

public interface RowMapper<T> extends SheetMapper<T>, Mapper<Row, T> {
}

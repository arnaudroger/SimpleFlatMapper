package org.simpleflatmapper.poi;


import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SourceMapper;

public interface RowMapper<T> extends SheetMapper<T>, SourceMapper<Row, T>, FieldMapper<Row, T> {
}

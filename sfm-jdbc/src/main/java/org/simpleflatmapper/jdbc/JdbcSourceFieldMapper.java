package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactoryFromRows;

import java.sql.ResultSet;
import java.sql.SQLException;



public interface JdbcSourceFieldMapper<T> extends SourceFieldMapper<ResultSet, T>, MappingContextFactoryFromRows<ResultSet, ResultSet, SQLException> {


	
}

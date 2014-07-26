package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.map.Mapper;
import org.sfm.utils.Handler;

public interface ResultSetMapper<T> extends Mapper<ResultSet, T> {
	
	<H extends Handler<T>> H forEach(ResultSet rs, H handle) throws Exception;
	<H extends Handler<T>> H forEach(PreparedStatement statement, H handle) throws Exception;
	
}

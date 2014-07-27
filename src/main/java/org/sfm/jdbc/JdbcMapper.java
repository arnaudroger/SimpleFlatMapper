package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.sfm.map.Mapper;
import org.sfm.utils.Handler;

public interface JdbcMapper<T> extends Mapper<ResultSet, T> {
	
	<H extends Handler<T>> H forEach(ResultSet rs, H handle) throws Exception;
	<H extends Handler<T>> H forEach(PreparedStatement ps, H handle) throws Exception;
	
	List<T> list(ResultSet rs) throws Exception;
	List<T> list(PreparedStatement ps) throws Exception;
}

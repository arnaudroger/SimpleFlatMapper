package org.sfm.benchmark;

import java.sql.ResultSet;

public interface RowMapper<T> {
	void map(ResultSet rs, T o) throws Exception;
}

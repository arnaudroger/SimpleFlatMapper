package org.sfm.benchmark;

import java.sql.ResultSet;

public interface RowMapper<T> {
	T map(ResultSet rs) throws Exception;
}

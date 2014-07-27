package org.sfm.benchmark;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.sfm.beans.DbObject;

public interface DbObjectMapper {
	 @Select("SELECT * FROM test_db_object ")
	 List<DbObject> selectDbObjects();
}

package org.sfm.benchmark.ibatis;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.sfm.beans.DbObject;

public interface DbObjectMapper {
	 @Select("SELECT * FROM test_db_object ")
	 List<DbObject> selectDbObjects();
	 
	 @Select("SELECT * FROM test_db_object LIMIT #{limit}")
	 List<DbObject> selectDbObjectsWithLimit(int limit);
	 
	 @Select("SELECT id, name, email, year_started FROM test_small_benchmark_object ")
	 List<DbObject> selectSmallBenchmarkObjects();
	 
	 @Select("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT #{limit}")
	 List<DbObject> selectSmallBenchmarkObjectsWithLimit(int limit);
}

package org.sfm.benchmark;

import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.beans.SmallBenchmarkObject;

public class JDBCHelper {
	public static String query(Class<?> target, int limit) {
		StringBuilder query;
		if (target.equals(DbObject.class)) {
			query= new StringBuilder("SELECT id, name, email, creation_time FROM test_db_object ");
		} else  if (target.equals(SmallBenchmarkObject.class)) {
			query= new StringBuilder("SELECT id, name, email, year_started FROM test_small_benchmark_object ");
		} else {
			throw new UnsupportedOperationException("Invalid target " + target);
		}
		if (limit >= 0) {
			query.append("LIMIT ").append(Integer.toString(limit));
		}
		return query.toString();
	}
	
	public static <T> RowMapper<T> mapper(Class<T> target) {
		if (target.equals(DbObject.class)) {
			return new RowMapper<T>() {
				@SuppressWarnings("unchecked")
				@Override
				public T map(ResultSet rs)
						throws Exception {
					DbObject o = new DbObject();
					o.setId(rs.getLong(1));
					o.setName(rs.getString(2));
					o.setEmail(rs.getString(3));
					o.setCreationTime(rs.getTimestamp(4));
					return (T) o;
				}
			};
		} else if (target.equals(SmallBenchmarkObject.class)) {
			return new RowMapper<T>() {
				@SuppressWarnings("unchecked")
				@Override
				public T map(ResultSet rs)
						throws Exception {
					SmallBenchmarkObject o = new SmallBenchmarkObject();
					o.setId(rs.getLong(1));
					o.setName(rs.getString(2));
					o.setEmail(rs.getString(3));
					o.setYearStarted(rs.getInt(4));
					return (T) o;
				}
			};
		}
		
		throw new UnsupportedOperationException("No mapper for " + target);
	}
}

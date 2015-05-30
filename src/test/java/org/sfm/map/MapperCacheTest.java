package org.sfm.map;

import org.junit.Test;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.map.impl.MapperCache;

import java.sql.SQLException;
import java.text.ParseException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class MapperCacheTest {

	@Test
	public void testMapperCache() throws SQLException, ParseException, Exception {
		MapperCache<ColumnsMapperKey<CsvColumnKey>, Object> cache = new MapperCache<ColumnsMapperKey<CsvColumnKey>, Object>();
		ColumnsMapperKey key = new ColumnsMapperKey(new CsvColumnKey("col1", 1), new CsvColumnKey("col2", 2));
		Object delegate = cache.get(key);
		assertNull(delegate);
		delegate = new Object();
		cache.add(key, delegate);
		assertSame(delegate, cache.get(key));
	}
}

package org.sfm.map;

import org.junit.Test;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.map.impl.MapperCache;

import java.sql.SQLException;
import java.text.ParseException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class MapperCacheTest {

	@Test
	public void testMapperCache() throws SQLException, ParseException, Exception {
		MapperCache<ColumnsMapperKey, Object> cache = new MapperCache<ColumnsMapperKey, Object>();
		ColumnsMapperKey key = new ColumnsMapperKey("col1", "col2");
		Object delegate = cache.get(key);
		assertNull(delegate);
		delegate = new Object();
		cache.add(key, delegate);
		assertSame(delegate, cache.get(key));
	}
}

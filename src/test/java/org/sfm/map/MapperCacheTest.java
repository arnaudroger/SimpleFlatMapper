package org.sfm.map;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.text.ParseException;

import org.junit.Test;

public class MapperCacheTest {

	@Test
	public void testMapperCache() throws SQLException, ParseException, Exception {
		MapperCache<Object> cache = new MapperCache<Object>();
		MapperKey key = new MapperKey("col1", "col2");
		Object delegate = cache.get(key);
		assertNull(delegate);
		delegate = new Object();
		cache.add(key, delegate);
		assertSame(delegate, cache.get(key));
	}
}

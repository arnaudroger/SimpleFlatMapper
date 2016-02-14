package org.sfm.map;

import org.junit.Test;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.impl.FieldKeyComparator;
import org.sfm.map.mapper.MapperCache;
import org.sfm.map.mapper.MapperKey;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class MapperCacheTest {

	@Test
	public void testMapperCache() throws Exception {
		MapperCache<CsvColumnKey, Object> cache = new MapperCache<CsvColumnKey, Object>(new FieldKeyComparator());
		MapperKey<CsvColumnKey> key = new MapperKey<CsvColumnKey> (new CsvColumnKey("col1", 1), new CsvColumnKey("col2", 2));
		Object delegate = cache.get(key);
		assertNull(delegate);
		delegate = new Object();
		cache.add(key, delegate);
		assertSame(delegate, cache.get(key));
	}
}

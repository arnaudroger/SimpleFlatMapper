package org.sfm.map;

import org.junit.Test;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.mapper.MapperCache;
import org.sfm.map.mapper.MapperKey;
import org.sfm.map.mapper.MapperKeyComparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class MapperCacheTest {

	@Test
	public void testMapperCacheLimits() throws Exception {
		MapperCache<CsvColumnKey, Object> cache = new MapperCache<CsvColumnKey, Object>(MapperKeyComparator.csvColumnKeyComparator());

		Object[] mappers = new Object[100];
		for(int i = 0 ; i < 100; i++) {
			MapperKey<CsvColumnKey> key = new MapperKey<CsvColumnKey> (new CsvColumnKey("col" + i, 1), new CsvColumnKey("col" + i + 1, 2));
			assertNull(cache.get(key));
			Object o = new Object();

			cache.add(key, o);
			assertEquals(o, cache.get(key));
			mappers[i] = o;

			cache.add(key, new Object());
			assertEquals(o, cache.get(key));


			for(int j = 0 ; j < i; j++) {
				MapperKey<CsvColumnKey> key2 = new MapperKey<CsvColumnKey> (new CsvColumnKey("col" + j, 1), new CsvColumnKey("col" + j + 1, 2));
				assertEquals(mappers[j], cache.get(key2));

			}

		}

	}


}

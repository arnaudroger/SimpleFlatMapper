package org.sfm.map;

import org.junit.Test;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.mapper.MapperCache;
import org.sfm.map.mapper.MapperKey;
import org.sfm.map.mapper.MapperKeyComparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MapperCacheTest {

	@Test
	public void testMapperCacheLimits() throws Exception {
		MapperCache<JdbcColumnKey, Object> cache = new MapperCache<JdbcColumnKey, Object>(MapperKeyComparator.jdbcColumnKeyComparator());

		Object[] mappers = new Object[100];
		for(int i = 0 ; i < 100; i++) {
			MapperKey<JdbcColumnKey> key = new MapperKey<JdbcColumnKey> (new JdbcColumnKey("col" + i, 1), new JdbcColumnKey("col" + i + 1, 2));
			assertNull(cache.get(key));
			Object o = new Object();

			cache.add(key, o);
			assertEquals(o, cache.get(key));
			mappers[i] = o;

			cache.add(key, new Object());
			assertEquals(o, cache.get(key));


			for(int j = 0 ; j < i; j++) {
				MapperKey<JdbcColumnKey> key2 = new MapperKey<JdbcColumnKey> (new JdbcColumnKey("col" + j, 1), new JdbcColumnKey("col" + j + 1, 2));
				assertEquals(mappers[j], cache.get(key2));

			}

		}

	}


}

package org.simpleflatmapper.test.map;

import org.junit.Test;
import org.simpleflatmapper.map.mapper.MapperCache;
import org.simpleflatmapper.map.mapper.MapperKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MapperCacheTest {

	@Test
	public void testMapperCacheLimits() throws Exception {
		MapperCache<SampleFieldKey, Object> cache = new MapperCache<SampleFieldKey, Object>(SampleFieldKeyMapperKeyComparator.INSTANCE);

		Object[] mappers = new Object[100];
		for(int i = 0 ; i < 100; i++) {
			MapperKey<SampleFieldKey> key = new MapperKey<SampleFieldKey> (new SampleFieldKey("col" + i, 1), new SampleFieldKey("col" + i + 1, 2));
			assertNull(cache.get(key));
			Object o = new Object();

			cache.add(key, o);
			assertEquals(o, cache.get(key));
			mappers[i] = o;

			cache.add(key, new Object());
			assertEquals(o, cache.get(key));


			for(int j = 0 ; j < i; j++) {
				MapperKey<SampleFieldKey> key2 = new MapperKey<SampleFieldKey> (new SampleFieldKey("col" + j, 1), new SampleFieldKey("col" + j + 1, 2));
				assertEquals(mappers[j], cache.get(key2));

			}

		}

	}


}

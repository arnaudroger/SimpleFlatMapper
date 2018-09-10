package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.UnaryFactory;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.UnaryFactoryWithException;

import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END


public class DynamicSetRowMapper<ROW, SET, T, E extends Exception, K extends FieldKey<K>> implements SetRowMapper<ROW, SET, T, E> {


    private final MapperCache<K, SetRowMapper<ROW, SET, T, E>> mapperCache;

	private final UnaryFactory<MapperKey<K>, SetRowMapper<ROW, SET, T, E>> mapperFactory;

	private final UnaryFactoryWithException<ROW, MapperKey<K>, E> mapperKeyFromRow;

	private final UnaryFactoryWithException<SET, MapperKey<K>, E> mapperKeyFromSet;

	public DynamicSetRowMapper(
			UnaryFactory<MapperKey<K>, SetRowMapper<ROW, SET, T, E>> mapperFactory,
			UnaryFactoryWithException<ROW, MapperKey<K>, E> mapperKeyFromRow,
			UnaryFactoryWithException<SET, MapperKey<K>, E> mapperKeyFromSet,
			MapperKeyComparator<K> keyComparator) {
		this.mapperFactory = mapperFactory;
		this.mapperKeyFromRow = mapperKeyFromRow;
		this.mapperKeyFromSet = mapperKeyFromSet;
		this.mapperCache = new MapperCache<K, SetRowMapper<ROW, SET, T, E>>(keyComparator);
	}

	@Override
	public final T map(ROW row) throws MappingException {
		try {
			return getMapperFromRow(row).map(row);
		} catch(Exception e) {
			ErrorHelper.rethrow(e);
			return null;
		}
	}
	
	@Override
	public final T map(ROW row, MappingContext<? super ROW> context) throws MappingException {
		try {
			return getMapperFromRow(row).map(row, context);
		} catch(Exception e) {
			ErrorHelper.rethrow(e);
			return null;
		}
	}

	@Override
	public final Iterator<T> iterator(SET rs) throws E, MappingException {
		return getMapperFromSet(rs).iterator(rs);
	}

	//IFJAVA8_START
	@Override
	public final Stream<T> stream(SET set) throws E, MappingException {
		return getMapperFromSet(set).stream(set);
	}


	//IFJAVA8_END

	@Override
	public Enumerable<T> enumerate(SET source) throws E, MappingException {
		return getMapperFromSet(source).enumerate(source);
	}
	
	@Override
	public final <H extends CheckedConsumer<? super T>> H forEach(SET set, H handler) throws E, MappingException {
		return getMapperFromSet(set).forEach(set, handler);
	}

	@Override
	public String toString() {
		return "DynamicMapper{mapperFactory=" + mapperFactory
				+  ", " + mapperCache +
				'}';
	}

	private SetRowMapper<ROW, SET, T, E> getMapperFromSet(SET set) throws E {
		return getMapper(mapperKeyFromSet.newInstance(set));
	}

	private SetRowMapper<ROW, SET, T, E> getMapperFromRow(ROW row) throws E {
		return getMapper(mapperKeyFromRow.newInstance(row));
	}

	public SetRowMapper<ROW, SET, T, E> getMapper(MapperKey<K> key) throws E {
		SetRowMapper<ROW, SET, T, E> mapper = mapperCache.get(key);
		if (mapper == null) {
			mapper = mapperFactory.newInstance(key);
			mapperCache.add(key, mapper);
		}
		return mapper;
	}
}

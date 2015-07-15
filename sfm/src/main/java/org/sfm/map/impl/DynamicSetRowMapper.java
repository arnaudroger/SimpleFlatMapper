package org.sfm.map.impl;

import org.sfm.map.FieldKey;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingException;
import org.sfm.map.SetRowMapper;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.UnaryFactory;
import org.sfm.utils.RowHandler;
import org.sfm.utils.UnaryFactoryWithException;

import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END


public class DynamicSetRowMapper<R, S, T, E extends Exception, K extends FieldKey<K>> implements SetRowMapper<R, S, T, E> {


    private final MapperCache<K, SetRowMapper<R, S, T, E>> mapperCache = new MapperCache<K, SetRowMapper<R, S, T, E>>();

	private final UnaryFactory<MapperKey<K>, SetRowMapper<R, S, T, E>> mapperFactory;

	private final UnaryFactoryWithException<R, MapperKey<K>, E> mapperKeyFromRow;

	private final UnaryFactoryWithException<S, MapperKey<K>, E> mapperKeyFromSet;

	public DynamicSetRowMapper(
			UnaryFactory<MapperKey<K>, SetRowMapper<R, S, T, E>> mapperFactory,
			UnaryFactoryWithException<R, MapperKey<K>, E> mapperKeyFromRow,
			UnaryFactoryWithException<S, MapperKey<K>, E> mapperKeyFromSet) {
		this.mapperFactory = mapperFactory;
		this.mapperKeyFromRow = mapperKeyFromRow;
		this.mapperKeyFromSet = mapperKeyFromSet;
	}

	@Override
	public final T map(R row) throws MappingException {
		try {
			return getMapperFromRow(row).map(row);
		} catch(Exception e) {
			ErrorHelper.rethrow(e);
			return null;
		}
	}

	@Override
	public final T map(R row, MappingContext<? super R> context) throws MappingException {
		try {
			return getMapperFromRow(row).map(row, context);
		} catch(Exception e) {
			ErrorHelper.rethrow(e);
			return null;
		}
	}

	@Override
	public final void mapTo(R row, T target, MappingContext<? super R> context) throws Exception {
		getMapperFromRow(row).mapTo(row, target, context);
	}

	@Override
	public final Iterator<T> iterator(S rs) throws E, MappingException {
		return getMapperFromSet(rs).iterator(rs);
	}

	//IFJAVA8_START
	@Override
	public final Stream<T> stream(S set) throws E, MappingException {
		return getMapperFromSet(set).stream(set);
	}
	//IFJAVA8_END

	@Override
	public final <H extends RowHandler<? super T>> H forEach(S set, H handler) throws E, MappingException {
		return getMapperFromSet(set).forEach(set, handler);
	}

	@Override
	public final MappingContext<? super S> newMappingContext(S set) throws E {
		return getMapperFromSet(set).newMappingContext(set);
	}

	@Override
	public String toString() {
		return "DynamicMapper{mapperFactory=" + mapperFactory
				+  ", " + mapperCache +
				'}';
	}

	private SetRowMapper<R, S, T, E> getMapperFromSet(S set) throws E {
		return getMapper(mapperKeyFromSet.newInstance(set));
	}

	private SetRowMapper<R, S, T, E> getMapperFromRow(R row) throws E {
		return getMapper(mapperKeyFromRow.newInstance(row));
	}

	public SetRowMapper<R, S, T, E> getMapper(MapperKey<K> key) throws E {
		SetRowMapper<R, S, T, E> mapper = mapperCache.get(key);
		if (mapper == null) {
			mapper = mapperFactory.newInstance(key);
			mapperCache.add(key, mapper);
		}
		return mapper;
	}
}

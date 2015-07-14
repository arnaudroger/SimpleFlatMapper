package org.sfm.map;

import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.MapperCache;
import org.sfm.map.impl.MapperConfig;
import org.sfm.map.impl.MapperKey;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.OneArgumentFactory;
import org.sfm.utils.RowHandler;
import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END


public final class DynamicSetRowMapper<R, S, T, E extends Exception, K extends FieldKey<K>> implements SetRowMapper<R, S, T, E> {


    private final MapperCache<MapperKey<K>, SetRowMapper<R, S, T, E>> mapperCache = new MapperCache<MapperKey<K>, SetRowMapper<R, S, T, E>>();

	private final OneArgumentFactory<MapperKey<K>, SetRowMapper<R, S, T, E>> mapperFactory;


	public DynamicSetRowMapper(OneArgumentFactory<MapperKey<K>, SetRowMapper<R, S, T, E>> mapperFactory) {
		this.mapperFactory = mapperFactory;
	}

	@Override
	public T map(R row) throws MappingException {
		try {
			return getMapperFromRow(row).map(row);
		} catch(Exception e) {
			ErrorHelper.rethrow(e);
			return null;
		}
	}

	@Override
	public T map(R row, MappingContext<? super R> context) throws MappingException {
		try {
			return getMapperFromRow(row).map(row, context);
		} catch(Exception e) {
			ErrorHelper.rethrow(e);
			return null;
		}
	}

	@Override
	public void mapTo(R row, T target, MappingContext<? super R> context) throws Exception {
		getMapperFromRow(row).mapTo(row, target, context);
	}

	@Override
	public Iterator<T> iterator(S rs) throws E, MappingException {
		return getMapperFromSet(rs).iterator(rs);
	}

	//IFJAVA8_START
	@Override
	public Stream<T> stream(S rs) throws E, MappingException {
		return getMapperFromSet(rs).stream(rs);
	}
	//IFJAVA8_END

	@Override
	public <H extends RowHandler<? super T>> H forEach(S rs, H handler) throws E, MappingException {
		return getMapperFromSet(rs).forEach(rs, handler);
	}

	protected SetRowMapper<R, S, T, E> getMapperFromSet(S rs) throws E {
		return getMapper(getMapperKeyForSet(rs));
 	}

	protected SetRowMapper<R, S, T, E> getMapperFromRow(R rs) throws E {
		return getMapper(getMapperKeyForRow(rs));
	}

	@Override
	public MappingContext<? super S> newMappingContext(S rs) throws E {
		return getMapperFromSet(rs).newMappingContext(rs);
	}

	@Override
	public String toString() {
		return "DynamicMapper{mapperFactory=" + mapperFactory
				+  ", " + mapperCache +
				'}';
	}

	protected MapperKey<K> getMapperKeyForSet(S rs) throws E {
		return null;
	}
	protected MapperKey<K> getMapperKeyForRow(R rs) throws E {
		return null;
	}

	private SetRowMapper<R, S, T, E> getMapper(MapperKey<K> key) throws E {
		SetRowMapper<R, S, T, E> mapper = mapperCache.get(key);
		if (mapper == null) {
			mapper = mapperFactory.newInstance(key);
			mapperCache.add(key, mapper);
		}
		return mapper;
	}
}

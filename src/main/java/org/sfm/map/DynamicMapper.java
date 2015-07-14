package org.sfm.map;

import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.MapperCache;
import org.sfm.map.impl.MapperConfig;
import org.sfm.map.impl.MapperKey;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

//IFJAVA8_START
import java.util.Iterator;
import java.util.stream.Stream;
//IFJAVA8_END


public final class DynamicMapper<R, S, T, E extends Exception, K extends FieldKey<K>> implements SetRowMapper<R, S, T, E> {

	private final ClassMeta<T> classMeta;

    private final MapperCache<MapperKey<K>, SetRowMapper<R, S, T, E>> mapperCache = new MapperCache<MapperKey<K>, SetRowMapper<R, S, T, E>>();

	private final MapperConfig<K, FieldMapperColumnDefinition<K, S>> mapperConfig;

	private final GetterFactory<S, K> getterFactory;

	public DynamicMapper(final ClassMeta<T> classMeta,
						 MapperConfig<K, FieldMapperColumnDefinition<K, S>> mapperConfig,
						 GetterFactory<S, K> getterFactory) {
		this.classMeta = classMeta;
		this.mapperConfig = mapperConfig;
		this.getterFactory = getterFactory;
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
		return "DynamicMapper{target=" + classMeta.getType()
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
			final JdbcMapperBuilder<T> builder =
					new JdbcMapperBuilder<T>(
							classMeta,
							mapperConfig,
							getterFactory,
							new JdbcMappingContextFactoryBuilder());

			for(K k : key.getColumns()) {
				builder.addMapping(k, FieldMapperColumnDefinition.<K, S>identity());
			}

			mapper = builder.mapper();

			mapperCache.add(key, mapper);
		}
		return mapper;
	}
}

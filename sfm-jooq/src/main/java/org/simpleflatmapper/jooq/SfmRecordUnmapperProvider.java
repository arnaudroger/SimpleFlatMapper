package org.simpleflatmapper.jooq;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.RecordType;
import org.jooq.RecordUnmapper;
import org.jooq.RecordUnmapperProvider;
import org.jooq.impl.DSL;
import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.Function;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Integration point with jooq.<p>
 * Provide a JooqRecordMapper backed by an Sfm {@link org.jooq.Record} {@link SourceMapper}
 */
public class SfmRecordUnmapperProvider implements RecordUnmapperProvider {

	private final ConcurrentMap<TargetColumnsMapperKey, ContextualSourceMapper> mapperCache = new ConcurrentHashMap<TargetColumnsMapperKey, ContextualSourceMapper>();
	private final Function<Type, MapperConfig<JooqFieldKey, org.jooq.Record>> mapperConfigFactory;
	private final ReflectionService reflectionService;
	private final DSLContextProvider dslContextProvider;


	@Deprecated
	/**
	 * please use SfmRecorMapperProviderFactory.
	 */
	public SfmRecordUnmapperProvider(
			Function<Type, MapperConfig<JooqFieldKey, org.jooq.Record>> mapperConfigFactory, ReflectionService reflectionService, final Configuration configuration) {
		this(mapperConfigFactory, reflectionService, new DSLContextProvider() {
			@Override
			public DSLContext provide() {
				return DSL.using(configuration);
			}
		});
	}
	@Deprecated
	/**
	 * please use SfmRecorMapperProviderFactory.
	 */
	public SfmRecordUnmapperProvider(
			Function<Type, MapperConfig<JooqFieldKey, org.jooq.Record>> mapperConfigFactory, ReflectionService reflectionService, DSLContextProvider dslContextProvider) {
		this.mapperConfigFactory = mapperConfigFactory;
		this.reflectionService = reflectionService;
		this.dslContextProvider = dslContextProvider;
	}

	@Override
	public <E, R extends org.jooq.Record> RecordUnmapper<E, R> provide(Class<? extends E> type, RecordType<R> recordType) {

		TargetColumnsMapperKey key = getMapperKey(recordType, type);

		ContextualSourceMapper mapper = mapperCache.get(key);

		if (mapper == null) {
			MapperConfig<JooqFieldKey, org.jooq.Record> mapperConfig = mapperConfigFactory.apply(type);

			RecordUnmapperBuilder<E> mapperBuilder =
					new RecordUnmapperBuilder<E>(
							reflectionService.<E>getClassMeta(type),
							mapperConfig, dslContextProvider);

			mapperBuilder.setFields(recordType.fields());

			mapper = mapperBuilder.mapper();

			mapperCache.putIfAbsent(key, mapper);
		}
		
		return new JooqRecordUnmapperWrapper<E, R>(mapper);
	}

	private <R extends org.jooq.Record> TargetColumnsMapperKey getMapperKey(RecordType<R> recordType, Class<?> type) {
		String[] columns = new String[recordType.size()];
		int i = 0;
		for(Field<?> field : recordType.fields()) {
			columns[i++] = field.getName();
		}
		
		return new TargetColumnsMapperKey(type, columns);
	}
}

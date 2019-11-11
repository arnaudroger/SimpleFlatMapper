package org.simpleflatmapper.jooq;

import org.jooq.*;
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
 * Provide a JooqRecordMapper backed by an Sfm {@link Record} {@link SourceMapper}
 */
public class SfmRecordUnmapperProvider implements RecordUnmapperProvider {

	private final ConcurrentMap<TargetColumnsMapperKey, ContextualSourceMapper> mapperCache = new ConcurrentHashMap<TargetColumnsMapperKey, ContextualSourceMapper>();
	private final Function<Type, MapperConfig<JooqFieldKey, Record>> mapperConfigFactory;
	private final ReflectionService reflectionService;
	private final Configuration configuration;

	@Deprecated
	/**
	 * please use SfmRecorMapperProviderFactory.
	 */
	public SfmRecordUnmapperProvider(
			Function<Type, MapperConfig<JooqFieldKey, Record>> mapperConfigFactory, ReflectionService reflectionService, Configuration configuration) {
		this.mapperConfigFactory = mapperConfigFactory;
		this.reflectionService = reflectionService;
		this.configuration = configuration;
	}

	@Override
	public <E, R extends Record> RecordUnmapper<E, R> provide(Class<? extends E> type, RecordType<R> recordType) {

		TargetColumnsMapperKey key = getMapperKey(recordType, type);

		ContextualSourceMapper mapper = mapperCache.get(key);

		if (mapper == null) {
			MapperConfig<JooqFieldKey, Record> mapperConfig = mapperConfigFactory.apply(type);

			RecordUnmapperBuilder<E> mapperBuilder =
					new RecordUnmapperBuilder<E>(
							reflectionService.<E>getClassMeta(type),
							mapperConfig, configuration);

			mapperBuilder.setFields(recordType.fields());

			int i = 0;
			for(Field<?> field : recordType.fields()) {
				mapperBuilder.addColumn(new JooqFieldKey(field, i++));
			}

			mapper = mapperBuilder.mapper();

			mapperCache.putIfAbsent(key, mapper);
		}
		
		return new JooqRecordUnmapperWrapper<E, R>(mapper);
	}

	private <R extends Record> TargetColumnsMapperKey getMapperKey(RecordType<R> recordType, Class<?> type) {
		String[] columns = new String[recordType.size()];
		int i = 0;
		for(Field<?> field : recordType.fields()) {
			columns[i++] = field.getName();
		}
		
		return new TargetColumnsMapperKey(type, columns);
	}
}

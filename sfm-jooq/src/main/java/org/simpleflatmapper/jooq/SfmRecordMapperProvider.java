package org.simpleflatmapper.jooq;

import org.jooq.*;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.Function;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Integration point with jooq.<p>
 * Provide a JooqRecordMapper backed by an Sfm {@link org.jooq.Record} {@link SourceMapper}
 */
public class SfmRecordMapperProvider implements RecordMapperProvider {

	private final ConcurrentMap<TargetColumnsMapperKey, MapperAndContext> mapperCache = new ConcurrentHashMap<TargetColumnsMapperKey, MapperAndContext>();
	private final Function<Type, MapperConfig<JooqFieldKey, Record>> mapperConfigFactory;
	private final ReflectionService reflectionService;

	@Deprecated
	/**
	 * please use SfmRecorMapperProviderFactory.
	 */
	public SfmRecordMapperProvider() {
		this(new Function<Type, MapperConfig<JooqFieldKey, Record>>() {
			@Override
			public MapperConfig<JooqFieldKey, Record> apply(Type type) {
				return MapperConfig.<JooqFieldKey, Record>fieldMapperConfig();
			}
		}, ReflectionService.newInstance());
	}

	@Deprecated
	/**
	 * please use SfmRecorMapperProviderFactory.
	 */
	public SfmRecordMapperProvider(
			Function<Type, MapperConfig<JooqFieldKey, Record>> mapperConfigFactory, ReflectionService reflectionService) {
		this.mapperConfigFactory = mapperConfigFactory;
		this.reflectionService = reflectionService;
	}

	@Override
	public <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> type) {

		SourceMapper<Record, E> mapper;
		MappingContextFactory<? super Record> mappingContextFactory;

		TargetColumnsMapperKey key = getMapperKey(recordType, type);

		MapperAndContext mc = mapperCache.get(key);
		
				
		if (mc == null) {
			MapperConfig<JooqFieldKey, Record> mapperConfig = mapperConfigFactory.apply(type);

			JooqMapperBuilder<E> mapperBuilder =
					new JooqMapperBuilder<E>(
							reflectionService.<E>getClassMeta(type),
							new JooqMappingContextFactoryBuilder<Record>(!mapperConfig.unorderedJoin()),
							mapperConfig);

			int i = 0;
			for(Field<?> field : recordType.fields()) {
				mapperBuilder.addField(new JooqFieldKey(field, i++));
			}

			mapper = mapperBuilder.mapper();
			mappingContextFactory = mapperBuilder.contextFactory();
			
			mapperCache.putIfAbsent(key, new MapperAndContext(mapper, mappingContextFactory));
		} else {
			mapper = (SourceMapper<Record, E>) mc.mapper;
			mappingContextFactory = mc.mappingContextFactory;
		}
		
		return new JooqRecordMapperWrapper<R, E>(mapper, mappingContextFactory);
	}



	private <R extends Record> TargetColumnsMapperKey getMapperKey(RecordType<R> recordType, Class<?> type) {
		String[] columns = new String[recordType.size()];
		int i = 0;
		for(Field<?> field : recordType.fields()) {
			columns[i++] = field.getName();
		}
		
		return new TargetColumnsMapperKey(type, columns);
	}

	private class MapperAndContext {
		private final SourceMapper<Record, ?> mapper;
		private final MappingContextFactory<? super Record> mappingContextFactory;

		private MapperAndContext(SourceMapper<Record, ?> mapper, MappingContextFactory<? super Record> mappingContextFactory) {
			this.mapper = mapper;
			this.mappingContextFactory = mappingContextFactory;
		}
	}
}

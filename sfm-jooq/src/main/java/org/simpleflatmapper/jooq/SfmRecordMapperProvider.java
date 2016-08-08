package org.simpleflatmapper.jooq;

import org.jooq.*;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.ReflectionService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Integration point with jooq.<p>
 * Provide a JooqRecordMapper backed by an Sfm {@link org.jooq.Record} {@link Mapper}
 */
public class SfmRecordMapperProvider implements RecordMapperProvider {

	private final ConcurrentMap<TargetColumnsMapperKey, Mapper<Record, ?>> mapperCache = new ConcurrentHashMap<TargetColumnsMapperKey, Mapper<Record, ?>>();
	private final MapperConfig<JooqFieldKey, FieldMapperColumnDefinition<JooqFieldKey>> mapperConfig;
	private final ReflectionService reflectionService;

	public SfmRecordMapperProvider() {
		this(MapperConfig.<JooqFieldKey>fieldMapperConfig(), ReflectionService.newInstance());
	}

	public SfmRecordMapperProvider(
			MapperConfig<JooqFieldKey, FieldMapperColumnDefinition<JooqFieldKey>> mapperConfig, ReflectionService reflectionService) {
		this.mapperConfig = mapperConfig;
		this.reflectionService = reflectionService;
	}

	@Override
	public <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> type) {
		
		TargetColumnsMapperKey key = getMapperKey(recordType, type);
		
		@SuppressWarnings("unchecked")
		Mapper<Record, E> mapper = (Mapper<Record, E>) mapperCache.get(key);
		
		if (mapper == null) {
			mapper = buildMapper(recordType, type);
			mapperCache.putIfAbsent(key, mapper);
		}
		
		return new JooqRecordMapperWrapper<R, E>(mapper);
	}

	private <R extends Record, E> Mapper<Record, E> buildMapper(RecordType<R> recordType, Class<? extends E> type) {
		JooqMapperBuilder<E> mapperBuilder =
				new JooqMapperBuilder<E>(
						reflectionService.<E>getClassMeta(type),
						new JooqMappingContextFactoryBuilder<Record>(),
						mapperConfig);

		int i = 0;
		for(Field<?> field : recordType.fields()) {
            mapperBuilder.addField(new JooqFieldKey(field, i++));
        }

		return mapperBuilder.mapper();
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

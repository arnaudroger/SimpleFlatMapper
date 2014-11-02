package org.sfm.jooq;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordType;
import org.sfm.map.Mapper;
import org.sfm.map.MapperCache;
import org.sfm.map.TargetColumnsMapperKey;

public class SfmRecordMapperProvider implements RecordMapperProvider {

	private MapperCache<TargetColumnsMapperKey, Mapper<Record, ?>> mapperCache = new MapperCache<TargetColumnsMapperKey, Mapper<Record, ?>>();

	
	@Override
	public <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> type) {
		
		TargetColumnsMapperKey key = getMapperKey(recordType, type);
		
		@SuppressWarnings("unchecked")
		Mapper<Record, E> mapper = (Mapper<Record, E>) mapperCache.get(key);
		
		if (mapper == null) {
			JooqMapperBuilder<Record, E> mapperBuilder = new JooqMapperBuilder<Record, E>(type);
			
			int i = 0;
			for(Field<?> field : recordType.fields()) {
				mapperBuilder.addField(new JooqFieldKey(field, i++));
			}
			
			mapper = mapperBuilder.mapper();
			mapperCache.add(key, mapper);
		}
		
		return new RecordMapperWrapper<R, E>(mapper);
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

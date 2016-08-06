package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.jooq.RecordMapper;
import org.simpleflatmapper.map.Mapper;

public class JooqRecordMapperWrapper<R extends Record, E> implements RecordMapper<R, E> {

	private final Mapper<Record, E> mapper;

	public JooqRecordMapperWrapper(Mapper<Record, E> mapper) {
		this.mapper = mapper;
	}

	@Override
	public E map(R record) {
		return mapper.map(record);
	}

	public Mapper<Record, E> getMapper() {
		return mapper;
	}
}

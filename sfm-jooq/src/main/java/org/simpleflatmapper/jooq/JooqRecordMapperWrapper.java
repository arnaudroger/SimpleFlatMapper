package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.jooq.RecordMapper;
import org.simpleflatmapper.map.SourceMapper;

public class JooqRecordMapperWrapper<R extends Record, E> implements RecordMapper<R, E> {

	private final SourceMapper<Record, E> mapper;

	public JooqRecordMapperWrapper(SourceMapper<Record, E> mapper) {
		this.mapper = mapper;
	}

	@Override
	public E map(R record) {
		return mapper.map(record);
	}

	public SourceMapper<Record, E> getMapper() {
		return mapper;
	}
}

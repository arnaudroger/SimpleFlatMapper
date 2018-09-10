package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.jooq.RecordMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;

public class JooqRecordMapperWrapper<R extends Record, E> implements RecordMapper<R, E> {

	private final SourceMapper<Record, E> mapper;
	private final MappingContextFactory<? super Record> mappingContextFactory;

	public JooqRecordMapperWrapper(SourceMapper<Record, E> mapper, MappingContextFactory<? super Record> mappingContextFactory) {
		this.mapper = mapper;
		this.mappingContextFactory = mappingContextFactory;
	}

	@Override
	public E map(R record) {
		return mapper.map(record, mappingContextFactory.newContext());
	}

	public SourceMapper<Record, E> getMapper() {
		return mapper;
	}
}

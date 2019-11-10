package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordUnmapper;
import org.jooq.exception.MappingException;
import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;

public class JooqRecordUnmapperWrapper<E, R extends Record> implements RecordUnmapper<E, R> {

	private final ContextualSourceMapper<E, R> mapper;

	public JooqRecordUnmapperWrapper(ContextualSourceMapper<E, R> mapper) {
		this.mapper = mapper;
	}

	public ContextualSourceMapper<E, R> getMapper() {
		return mapper;
	}

	@Override
	public R unmap(E e) throws MappingException {
		return mapper.map(e);
	}
}

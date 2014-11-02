package org.sfm.jooq;

import org.jooq.Record;
import org.jooq.RecordMapper;
import org.sfm.map.Mapper;

public class RecordMapperWrapper<R extends Record, E> implements RecordMapper<R, E> {

	private Mapper<Record, E> mapper;

	public RecordMapperWrapper(Mapper<Record, E> mapper) {
		this.mapper = mapper;
	}

	@Override
	public E map(R record) {
		return mapper.map(record);
	}

}

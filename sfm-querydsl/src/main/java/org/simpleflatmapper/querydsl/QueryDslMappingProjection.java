package org.simpleflatmapper.querydsl;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MappingProjection;
import org.simpleflatmapper.map.Mapper;

public class QueryDslMappingProjection<T> extends MappingProjection<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9015755919878465141L;
	private final Mapper<Tuple, T> mapper;
	public QueryDslMappingProjection(Class<T> type, Expression<?>... args) {
		super(type, args);
		QueryDslMapperBuilder<T> builder =new QueryDslMapperBuilder<T>(type);
		for(int i = 0; i < args.length; i++) {
			builder.addMapping(args[i], i);
		}
		mapper = builder.mapper();
	}


	@Override
	protected T map(Tuple row) {
		return mapper.map(row);
	}


}

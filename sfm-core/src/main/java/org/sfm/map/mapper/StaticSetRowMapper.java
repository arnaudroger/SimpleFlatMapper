package org.sfm.map.mapper;

import org.sfm.map.*;
import org.sfm.map.context.MappingContextFactory;
import org.sfm.map.impl.StaticMapperEnumarable;
import org.sfm.map.mapper.AbstractEnumarableMapper;
import org.sfm.utils.Enumarable;
import org.sfm.utils.UnaryFactory;

public class StaticSetRowMapper<R, S, T, E extends Exception> extends AbstractEnumarableMapper<S, T, E> implements SetRowMapper<R, S, T, E> {

	private final Mapper<R, T> mapper;
	private final MappingContextFactory<? super  R> mappingContextFactory;
	private final UnaryFactory<S, Enumarable<R>> enumarableFactory;

	public StaticSetRowMapper(final Mapper<R, T> mapper,
							  final RowHandlerErrorHandler errorHandler,
							  final MappingContextFactory<? super R> mappingContextFactory,
							  UnaryFactory<S, Enumarable<R>> enumarableFactory) {
		super(errorHandler);
		this.mapper = mapper;
		this.mappingContextFactory = mappingContextFactory;
		this.enumarableFactory = enumarableFactory;
	}

	@Override
	public final T map(R rs) throws MappingException {
		return mapper.map(rs);
	}

	@Override
	public final T map(R rs, MappingContext<? super R> context) throws MappingException {
		return mapper.map(rs, context);
	}

	@Override
	public final void mapTo(R rs, T target, MappingContext<? super R> context) throws Exception {
		mapper.mapTo(rs, target, context);
	}

	@Override
	public final MappingContext<? super R> newMappingContext(R set) throws E {
		return mappingContextFactory.newContext();
	}

	@Override
	public String toString() {
		return "StaticSetRowMapper{" + mapper + '}';
	}

	@Override
	protected final Enumarable<T> newEnumarableOfT(S source) throws E {
		return new StaticMapperEnumarable<R, T>(mapper, mappingContextFactory.newContext(), enumarableFactory.newInstance(source));
	}
}

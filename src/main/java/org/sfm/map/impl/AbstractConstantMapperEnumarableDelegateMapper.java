package org.sfm.map.impl;

import org.sfm.map.*;
import org.sfm.utils.Enumarable;


public abstract class AbstractConstantMapperEnumarableDelegateMapper<R, S, T, E extends Exception> extends AbstractEnumarableDelegateMapper<R, S, T, E> {

	private final Mapper<R, T> mapper;
	private final MappingContextFactory<? super R> mappingContextFactory;

	public AbstractConstantMapperEnumarableDelegateMapper(final Mapper<R, T> mapper,
														  final RowHandlerErrorHandler errorHandler,
														  final MappingContextFactory<? super R> mappingContextFactory) {
		super(errorHandler);
		this.mapper = mapper;
		this.mappingContextFactory = mappingContextFactory;
	}

	@Override
	protected final Enumarable<T> newEnumarableOfT(S source) throws E {
		return new MapperEnumarable<R, T>(getMapperFromSource(source),  newEnumarableOfR(source));
	}

	protected abstract Mapper<R, T> getMapperFromSource(S source);

	protected abstract Enumarable<R> newEnumarableOfR(S source);
}

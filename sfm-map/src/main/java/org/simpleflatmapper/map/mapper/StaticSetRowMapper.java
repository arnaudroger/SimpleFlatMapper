package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.impl.StaticMapperEnumerable;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.UnaryFactory;

public class StaticSetRowMapper<ROW, SET, T, E extends Exception> extends AbstractEnumerableMapper<SET, T, E> implements SetRowMapper<ROW, SET, T, E> {

	private final ContextualSourceMapper<ROW, T> mapper;
	private final MappingContextFactory<? super ROW> mappingContextFactory;
	private final UnaryFactory<SET, Enumerable<ROW>> enumerableFactory;

	public StaticSetRowMapper(final ContextualSourceMapper<ROW, T> mapper,
							  final ConsumerErrorHandler errorHandler,
							  final MappingContextFactory<? super ROW> mappingContextFactory,
							  UnaryFactory<SET, Enumerable<ROW>> enumerableFactory) {
		super(errorHandler);
		this.mapper = mapper;
		this.mappingContextFactory = mappingContextFactory;
		this.enumerableFactory = enumerableFactory;
	}

	@Override
	public T map(ROW source) throws MappingException {
		return mapper.map(source);
	}

	@Override
	public final T map(ROW rs, MappingContext<? super ROW> context) throws MappingException {
		return mapper.map(rs, context);
	}

	@Override
	public String toString() {
		return "StaticSetRowMapper{" + mapper + '}';
	}

	@Override
	public final Enumerable<T> enumerate(SET source) throws E {
		return new StaticMapperEnumerable<ROW, T>(mapper, mappingContextFactory.newContext(), enumerableFactory.newInstance(source));
	}

	protected MappingContextFactory<? super ROW> getMappingContextFactory() {
		return mappingContextFactory;
	}
}

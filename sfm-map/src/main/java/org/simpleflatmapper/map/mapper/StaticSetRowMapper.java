package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.impl.StaticMapperEnumarable;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.UnaryFactory;

public class StaticSetRowMapper<ROW, SET, T, E extends Exception> extends AbstractEnumarableMapper<SET, T, E> implements SetRowMapper<ROW, SET, T, E> {

	private final Mapper<ROW, T> mapper;
	private final MappingContextFactory<? super ROW> mappingContextFactory;
	private final UnaryFactory<SET, Enumarable<ROW>> enumarableFactory;

	public StaticSetRowMapper(final Mapper<ROW, T> mapper,
							  final ConsumerErrorHandler errorHandler,
							  final MappingContextFactory<? super ROW> mappingContextFactory,
							  UnaryFactory<SET, Enumarable<ROW>> enumarableFactory) {
		super(errorHandler);
		this.mapper = mapper;
		this.mappingContextFactory = mappingContextFactory;
		this.enumarableFactory = enumarableFactory;
	}

	@Override
	public final T map(ROW rs) throws MappingException {
		return mapper.map(rs);
	}

	@Override
	public final T map(ROW rs, MappingContext<? super ROW> context) throws MappingException {
		return mapper.map(rs, context);
	}

	@Override
	public final void mapTo(ROW rs, T target, MappingContext<? super ROW> context) throws Exception {
		mapper.mapTo(rs, target, context);
	}

	@Override
	public String toString() {
		return "StaticSetRowMapper{" + mapper + '}';
	}

	@Override
	protected final Enumarable<T> newEnumarableOfT(SET source) throws E {
		return new StaticMapperEnumarable<ROW, T>(mapper, mappingContextFactory.newContext(), enumarableFactory.newInstance(source));
	}

	protected MappingContextFactory<? super ROW> getMappingContextFactory() {
		return mappingContextFactory;
	}
}

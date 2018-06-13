package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.util.Function;

public final class TransformSourceMapper<ROW, I, O> implements SourceMapper<ROW, O> {

	private final SourceMapper<ROW, I> delegate;
	private final Function<? super I, ? extends O> transformer;

	public TransformSourceMapper(SourceMapper<ROW, I> delegate, Function<I, O> transformer) {
		this.delegate = delegate;
		this.transformer = transformer;
	}

	@Override
	public O map(ROW source) throws MappingException {
		return transformer.apply(delegate.map(source));
	}

	@Override
	public O map(ROW source, MappingContext<? super ROW> context) throws MappingException {
		return transformer.apply(delegate.map(source, context));
	}
}

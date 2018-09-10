package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.TransformEnumerable;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.TransformCheckedConsumer;
import org.simpleflatmapper.util.TransformIterator;

import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public final class TransformSetRowMapper<ROW, SET, I, O, E extends Exception> implements SetRowMapper<ROW, SET, O, E> {

	private final SetRowMapper<ROW, SET, I, E> delegate;
	private final Function<? super I, ? extends O> transformer;

	public TransformSetRowMapper(SetRowMapper<ROW, SET, I, E> delegate, Function<I, O> transformer) {
		this.delegate = delegate;
		this.transformer = transformer;
	}

	@Override
	public <H extends CheckedConsumer<? super O>> H forEach(SET source, final H handler) throws E, MappingException {
		delegate.forEach(source, new TransformCheckedConsumer<I, O>(handler, transformer));
		return handler;
	}

	@Override
	public Iterator<O> iterator(SET source) throws E, MappingException {
		return new TransformIterator<I, O>(delegate.iterator(source), transformer);
	}

	@Override
	public Enumerable<O> enumerate(SET source) throws E, MappingException {
		return new TransformEnumerable<I, O>(delegate.enumerate(source), transformer);
	}

	//IFJAVA8_START
	@Override
	public Stream<O> stream(SET source) throws E, MappingException {
		return delegate.stream(source).map(transformer::apply);
	}
	//IFJAVA8_END


	@Override
	public O map(ROW source) throws MappingException {
		return transformer.apply(delegate.map(source));
	}

	@Override
	public O map(ROW source, MappingContext<? super ROW> context) throws MappingException {
		return transformer.apply(delegate.map(source, context));
	}
}

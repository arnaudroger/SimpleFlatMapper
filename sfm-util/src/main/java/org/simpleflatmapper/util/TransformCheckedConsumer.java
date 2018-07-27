package org.simpleflatmapper.util;

public final class TransformCheckedConsumer<I, O> implements CheckedConsumer<I> {
	private final CheckedConsumer<? super O> handler;
	private final Function<? super I, ? extends O> transformer;

	public TransformCheckedConsumer(CheckedConsumer<? super O> handler, Function<? super I, ? extends O> transformer) {
		this.handler = handler;
		this.transformer = transformer;
	}

	@Override
	public void accept(I i) throws Exception {
		handler.accept(transformer.apply(i));
	}
}
package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.reflect.Instantiator;

public class InstantiatorContextualGetter<S, T, P> implements ContextualGetter<T, P> {
	private final Instantiator<? super S, ? extends P> instantiator;
	private final ContextualGetter<? super T, ? extends S> getter;

	public InstantiatorContextualGetter(Instantiator<? super S, ? extends P> instantiator, ContextualGetter<? super T, ? extends S> getter) {
		this.instantiator = instantiator;
		this.getter = getter;
	}


	@Override
	public P get(T target, Context mappingContext) throws Exception {
		return instantiator.newInstance(getter.get(target, mappingContext));
	}

}

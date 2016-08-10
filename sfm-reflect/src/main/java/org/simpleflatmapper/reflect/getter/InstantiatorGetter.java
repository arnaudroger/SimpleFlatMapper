package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;

public class InstantiatorGetter<S, T, P> implements Getter<T, P> {
	private final Instantiator<? super S, ? extends P> instantiator;
	private final Getter<? super T, ? extends S> getter;
	
	public InstantiatorGetter(Instantiator<? super S, ? extends P> instantiator, Getter<? super T, ? extends S> getter) {
		super();
		this.instantiator = instantiator;
		this.getter = getter;
	}

	@Override
	public P get(T target) throws Exception {
		return instantiator.newInstance(getter.get(target));
	}

}

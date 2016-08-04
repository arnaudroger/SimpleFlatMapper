package org.simpleflatmapper.core.reflect;

public class InstantiatorOnGetter<S, T, P> implements Getter<T, P> {
	private final Instantiator<S, P> instantiator;
	private final Getter<T, S> getter;
	
	public InstantiatorOnGetter(Instantiator<S, P> instantiator, Getter<T, S> getter) {
		super();
		this.instantiator = instantiator;
		this.getter = getter;
	}

	@Override
	public P get(T target) throws Exception {
		return instantiator.newInstance(getter.get(target));
	}

}

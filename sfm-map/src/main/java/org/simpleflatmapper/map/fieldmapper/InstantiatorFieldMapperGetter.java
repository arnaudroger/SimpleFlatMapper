package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Instantiator;

public class InstantiatorFieldMapperGetter<S, T, P> implements FieldMapperGetter<T, P> {
	private final Instantiator<? super S, ? extends P> instantiator;
	private final FieldMapperGetter<? super T, ? extends S> getter;

	public InstantiatorFieldMapperGetter(Instantiator<? super S, ? extends P> instantiator, FieldMapperGetter<? super T, ? extends S> getter) {
		this.instantiator = instantiator;
		this.getter = getter;
	}


	@Override
	public P get(T target, MappingContext<?> mappingContext) throws Exception {
		return instantiator.newInstance(getter.get(target, mappingContext));
	}

}

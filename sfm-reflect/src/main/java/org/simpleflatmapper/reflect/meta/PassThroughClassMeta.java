package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class PassThroughClassMeta<T, V> implements ClassMeta<T> {

    private final ReflectionService reflectionService;
	private final Type type;
	private final InstantiatorDefinition instantiatorDefinition;
    private final PropertyMeta<T, V> propertyMeta;
	private final ClassMeta<V> innerMeta;


	public PassThroughClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
            String value = TypeHelper.toClass(type).getAnnotation(ReflectionService.PassThrough.class).value();
			ObjectClassMeta<T> objectClassMeta = new ObjectClassMeta<T>(type, reflectionService);
			this.instantiatorDefinition = objectClassMeta.getInstantiatorDefinitions().get(0);
			PropertyFinder.PropertyFilter predicate = PropertyFinder.PropertyFilter.trueFilter();
			this.propertyMeta = objectClassMeta
					.newPropertyFinder()
					.findProperty(DefaultPropertyNameMatcher.of(value), new Object[0], (TypeAffinity) null, predicate);
			this.innerMeta = propertyMeta.getPropertyClassMeta();
		} catch(Exception e) {
            ErrorHelper.rethrow(e);
            throw new IllegalStateException();
		}
	}

	public PassThroughClassMeta(ReflectionService reflectionService, Type type, InstantiatorDefinition instantiatorDefinition, PropertyMeta<T, V> propertyMeta, ClassMeta<V> innerMeta) {
		this.reflectionService = reflectionService;
		this.type = type;
		this.instantiatorDefinition = instantiatorDefinition;
		this.propertyMeta = propertyMeta;
		this.innerMeta = innerMeta;
	}

	@Override
	public ClassMeta<T> withReflectionService(ReflectionService reflectionService) {
		return new PassThroughClassMeta<T, V>(reflectionService, type, instantiatorDefinition, propertyMeta.withReflectionService(reflectionService), reflectionService.<V>getClassMeta(innerMeta.getType()));
	}


    @Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<T> newPropertyFinder() {
		return new PassThroughPropertyFinder<T, V>(this, reflectionService.selfScoreFullName());
	}

	public Type getType() {
		return type;
	}

	public ClassMeta<V> getInnerMeta() {
		return innerMeta;
	}

    public PropertyMeta<T, ?> getProperty() {
        return propertyMeta;
    }

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return Arrays.asList(instantiatorDefinition);
	}

	@Override
	public void forEachProperties(Consumer<? super PropertyMeta<T, ?>> consumer) {
		consumer.accept(propertyMeta);
	}

	@Override
	public int getNumberOfProperties() {
		return 1;
	}

	@Override
	public boolean needTransformer() {
		return false;
	}



}

package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.List;

public interface ClassMeta<T> {

	ReflectionService getReflectionService();

	PropertyFinder<T> newPropertyFinder();

	Type getType();

	List<InstantiatorDefinition> getInstantiatorDefinitions();

	void forEachProperties(Consumer<? super PropertyMeta<T, ?>> consumer);

    int getNumberOfProperties();

    boolean needTransformer();
    
    ClassMeta<T> withReflectionService(ReflectionService reflectionService);


}
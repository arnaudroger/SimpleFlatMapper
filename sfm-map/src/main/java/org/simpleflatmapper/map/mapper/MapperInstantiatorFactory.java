package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.impl.CalculateMaxIndex;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.instantiator.GetterToInstantiator;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.Map;

public class MapperInstantiatorFactory {

    private final InstantiatorFactory instantiatorFactory;

    public MapperInstantiatorFactory(InstantiatorFactory instantiatorFactory) {
        this.instantiatorFactory = instantiatorFactory;
    }

    public <S, T, K extends FieldKey<K>, D extends ColumnDefinition<K, D>> Instantiator<S,T> getInstantiator(Type source, Type target, PropertyMappingsBuilder<T, K, D> propertyMappingsBuilder, Map<Parameter, Getter<? super S, ?>> constructorParameterGetterMap, GetterFactory<? super S, K> getterFactory) throws NoSuchMethodException {
        return  getInstantiator(source, target, propertyMappingsBuilder, constructorParameterGetterMap, getterFactory, true);
    }

    @SuppressWarnings("unchecked")
    public <S, T, K extends FieldKey<K>, D extends ColumnDefinition<K, D>> Instantiator<S,T> getInstantiator(Type source, Type target, PropertyMappingsBuilder<T, K, D> propertyMappingsBuilder, Map<Parameter, Getter<? super S, ?>> constructorParameterGetterMap, final  GetterFactory<? super S, K> getterFactory, boolean useAsmIfEnabled) throws NoSuchMethodException {

        if (propertyMappingsBuilder.isDirectProperty()) {
            Getter getter = propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, K, D>>() {
                public Getter getter;
                @Override
                public void handle(PropertyMapping<T, ?, K, D> propertyMapping) {
                    getter = getterFactory.newGetter(propertyMapping.getPropertyMeta().getPropertyType(), propertyMapping.getColumnKey(), propertyMapping.getColumnDefinition().properties());
                }
            }).getter;

            return new GetterToInstantiator<S, T>(getter);

        }

        if (TypeHelper.isArray(target)) {
            return instantiatorFactory.getArrayInstantiator(TypeHelper.toClass(TypeHelper.getComponentTypeOfListOrArray(target)), propertyMappingsBuilder.forEachProperties(new CalculateMaxIndex<T, K, D>()).maxIndex + 1);
        } else {
            return instantiatorFactory.getInstantiator(target, TypeHelper.toClass(source), propertyMappingsBuilder.getPropertyFinder().getEligibleInstantiatorDefinitions(), constructorParameterGetterMap,useAsmIfEnabled);
        }
    }
}

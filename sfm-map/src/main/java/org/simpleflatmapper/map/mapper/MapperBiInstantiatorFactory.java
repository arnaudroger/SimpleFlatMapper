package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterBiFunction;
import org.simpleflatmapper.map.getter.ContextualGetterBiInstantiator;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapperBiInstantiatorFactory {

    private final InstantiatorFactory instantiatorFactory;

    public MapperBiInstantiatorFactory(InstantiatorFactory instantiatorFactory) {
        this.instantiatorFactory = instantiatorFactory;
    }


    public <S, T, K extends FieldKey<K>> BiInstantiator<S, MappingContext<?>, T> getBiInstantiator(Type source, Type target, PropertyMappingsBuilder<T, K> propertyMappingsBuilder, Map<Parameter, ContextualGetter<? super S, ?>> constructorParameterGetterMap, ContextualGetterFactory<? super S, K> getterFactory, boolean builderIgnoresNullValues, MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder) throws NoSuchMethodException {
        return  getBiInstantiator(source, target, propertyMappingsBuilder, constructorParameterGetterMap, getterFactory, true, builderIgnoresNullValues, mappingContextFactoryBuilder);
    }

    @SuppressWarnings("unchecked")
    public <S, T, K extends FieldKey<K>> BiInstantiator<S, MappingContext<?>, T> getBiInstantiator(final Type source, final Type target, final PropertyMappingsBuilder<T, K> propertyMappingsBuilder, final Map<Parameter, ContextualGetter<? super S, ?>> constructorParameterGetterMap, final ContextualGetterFactory<? super S, K> getterFactory, final boolean useAsmIfEnabled, final boolean builderIgnoresNullValues, final MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder) throws NoSuchMethodException {

        if (propertyMappingsBuilder.isSelfProperty()) {
            ContextualGetter getter = propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, K>>() {
                public ContextualGetter getter;
                @Override
                public void handle(PropertyMapping<T, ?, K> propertyMapping) {
                    getter = getterFactory.newGetter(propertyMapping.getPropertyMeta().getPropertyType(), propertyMapping.getColumnKey(), mappingContextFactoryBuilder, propertyMapping.getColumnDefinition().properties());
                }
            }).getter;
            return new ContextualGetterBiInstantiator<S, T>(getter);
        }

        if (TypeHelper.isArray(target)) {
            return instantiatorFactory.<S, MappingContext<?>, T>getArrayBiInstantiator(TypeHelper.toClass(TypeHelper.getComponentTypeOfListOrArray(target)), propertyMappingsBuilder.forEachProperties(new CalculateMaxIndex<T, K>()).maxIndex + 1);
        } else {
            List<InstantiatorDefinition> instantiatorDefinitions = propertyMappingsBuilder.getPropertyFinder().getEligibleInstantiatorDefinitions();
            return
                    instantiatorFactory.
                            <S, MappingContext<?>, T>
                                    getBiInstantiator(
                                            target,
                                            TypeHelper.<S>toClass(source),
                                            MappingContext.class,
                                            instantiatorDefinitions,
                                            convertToBiInstantiator(constructorParameterGetterMap),
                                            useAsmIfEnabled, builderIgnoresNullValues);
        }
    }

    public static <S> Map<Parameter, BiFunction<? super S, ? super MappingContext<?>, ?>> convertToBiInstantiator(Map<Parameter, ContextualGetter<? super S, ?>> constructorParameterGetterMap) {
        Map<Parameter, BiFunction<? super S, ? super MappingContext<?>, ?>> newMap = new HashMap<Parameter, BiFunction<? super S, ? super MappingContext<?>, ?>>(constructorParameterGetterMap.size());
        
        for(Map.Entry<Parameter, ContextualGetter<? super S, ?>> e : constructorParameterGetterMap.entrySet()) {
            newMap.put(e.getKey(), ContextualGetterBiFunction.of(e.getKey().getType(), e.getValue()));                 
        }
        
        return newMap;
    }

}

package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.impl.GenericBuilder;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.simpleflatmapper.map.mapper.DefaultConstantSourceMapperBuilder.EMPTY_FIELD_MAPPERS;

public class GenericBuilderMapperBuilder<S, T, K extends FieldKey<K>> {


    private ReflectionService reflectionService;
    private MapperSource<? super S, K> mapperSource;
    private MapperConfig<K> mappingConfig;

  
}

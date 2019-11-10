package org.simpleflatmapper.jooq;


import org.jooq.Field;
import org.jooq.Record;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.impl.*;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.AbstractConstantTargetMapperBuilder;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactory;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.setter.ContextualIndexedSetterFactory;
import org.simpleflatmapper.reflect.*;
import org.simpleflatmapper.reflect.impl.EmptyConstructorBiInstantiator;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;


public class RecordMapperBuilder<E, R extends Record> extends AbstractConstantTargetMapperBuilder<R, E, JooqFieldKey, RecordMapperBuilder<E, R>> {


    public RecordMapperBuilder(
            ClassMeta<E> classMeta,
            MapperConfig<JooqFieldKey, ?> mapperConfig,
            Class<R> recordClass) {
        super(classMeta, recordClass, mapperConfig, new ConstantTargetFieldMapperFactory<R, JooqFieldKey>() {
            @Override
            public <S, P> FieldMapper<S, R> newFieldMapper(PropertyMapping<S, P, JooqFieldKey> propertyMapping, MappingContextFactoryBuilder contextFactoryBuilder, MapperBuilderErrorHandler mappingErrorHandler) {
                final Getter<? super S, ? extends P> getter = propertyMapping.getPropertyMeta().getGetter();
                final Field<?> field = propertyMapping.getColumnKey().getField();
                final Type propertyType = propertyMapping.getPropertyMeta().getPropertyType();
                final Class<?> fieldType = field.getType();

                if (fieldType.isAssignableFrom(TypeHelper.toClass(propertyType))) {
                    return new FieldMapper<S, R>() {
                        @Override
                        public void mapTo(S source, R target, MappingContext<? super S> context) throws Exception {
                            P value = getter.get(source);
                            target.set((Field<P>)field, value);
                        }
                    };
                } else {
                    final Converter converter = ConverterService.getInstance().findConverter(propertyType, fieldType);
                    if (converter == null) throw new IllegalStateException("Cannot find converter from " + propertyType + " to " + fieldType + " for field " + field);
                    return new FieldMapper<S, R>() {
                        @Override
                        public void mapTo(S source, R target, MappingContext<? super S> context) throws Exception {
                            P value = getter.get(source);
                            target.set((Field)field, converter.convert(value));
                        }
                    };

                }
            }
        });
    }

    @Override
    protected BiInstantiator<E, MappingContext<? super E>, R> getInstantiator() {
        try {
            return new EmptyConstructorBiInstantiator<>(sourceClass.getConstructor());
        } catch (NoSuchMethodException e) {
            return ErrorHelper.rethrow(e);
        }
    }

    @Override
    protected JooqFieldKey newKey(String column, int i, FieldMapperColumnDefinition<JooqFieldKey> columnDefinition) {
        throw new UnsupportedOperationException();
    }


    protected int getStartingIndex() {
        return Integer.MIN_VALUE;
    }


}

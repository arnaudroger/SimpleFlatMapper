package org.simpleflatmapper.jooq;


import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.simpleflatmapper.converter.*;
import org.simpleflatmapper.map.*;
import org.simpleflatmapper.map.mapper.AbstractConstantTargetMapperBuilder;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactoryImpl;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.property.ConverterProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetterFactory;
import org.simpleflatmapper.reflect.*;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class RecordUnmapperBuilder<E> extends AbstractConstantTargetMapperBuilder<Record, E, JooqFieldKey, RecordUnmapperBuilder<E>> {


    private static final ContextualSetterFactory<Record, PropertyMapping<?,?, JooqFieldKey>> SETTER_FACTORY = new ContextualSetterFactory<Record, PropertyMapping<?, ?, JooqFieldKey>>() {

        @Override
        public <P> ContextualSetter<Record, P> getSetter(PropertyMapping<?, ?, JooqFieldKey> pm, ContextFactoryBuilder contextFactoryBuilder) {
            Type propertyType = pm.getPropertyMeta().getPropertyType();
            final Field<?> field = pm.getColumnKey().getField();
            Class<?> fieldType = field.getType();

            if (TypeHelper.isAssignable(fieldType, propertyType)) {
                return new RecordContextualSetter<P>((Field<? super P>) field);
            } else {
                ContextualConverter converter;
                if (pm.getColumnDefinition().has(ConverterProperty.class)) {
                    ConverterProperty converterProperty = pm.getColumnDefinition().lookFor(ConverterProperty.class);
                    converter = converterProperty.function;
                } else {
                    converter = ConverterService.getInstance().findConverter(propertyType, fieldType, contextFactoryBuilder, pm.getColumnDefinition().properties());
                }

                if (converter != null) {
                    return new RecordWithConverterContextualSetter(field, converter);
                }

            }
            return null;
        }
    };

    private Field[] fields;
    private final DSLContextProvider dslContextProvider;

    public RecordUnmapperBuilder(
            ClassMeta<E> classMeta,
            MapperConfig<JooqFieldKey, ?> mapperConfig,
            final Configuration configuration) {
        this(classMeta, mapperConfig, new DSLContextProvider() {
            @Override
            public DSLContext provide() {
                return DSL.using(configuration);
            }
        });
    }

    public RecordUnmapperBuilder(
            ClassMeta<E> classMeta,
            MapperConfig<JooqFieldKey, ?> mapperConfig,
            DSLContextProvider dslContextProvider) {
        super(classMeta, Record.class, mapperConfig, ConstantTargetFieldMapperFactoryImpl.newInstance(SETTER_FACTORY, Record.class));
        this.dslContextProvider = dslContextProvider;
    }

    @Override
    protected BiInstantiator<E, MappingContext<? super E>, Record> getInstantiator() {
        final Field[] fields = this.fields;
        final DSLContextProvider dslContextProvider = this.dslContextProvider;
        return new BiInstantiator<E, MappingContext<? super E>, Record>() {
            @Override
            public Record newInstance(E e, MappingContext<? super E> mappingContext) {
                return dslContextProvider.provide().newRecord(fields);
            }
        };
    }

    @Override
    protected JooqFieldKey newKey(String column, int i, FieldMapperColumnDefinition<JooqFieldKey> columnDefinition) {
        throw new UnsupportedOperationException();
    }


    protected int getStartingIndex() {
        return Integer.MIN_VALUE;
    }


    public void setFields(Field<?>[] fields) {
        this.fields = fields;
        int i = 0;
        for(Field f : fields) {
            addColumn(new JooqFieldKey(f, i++));
        }
    }
    private static class RecordContextualSetter<P> implements ContextualSetter<Record, P> {
        private final Field<? super P> field;

        public RecordContextualSetter(Field<? super P> field) {
            this.field = field;
        }

        @Override
        public void set(Record target, P value, Context context) {
            target.set(field, value);
        }
    }

    private static class RecordWithConverterContextualSetter<I, P> implements ContextualSetter<Record, P> {
        private final ContextualConverter<P, I> converter;
        private final Field<? super I> field;

        public RecordWithConverterContextualSetter(Field<? super I> field, ContextualConverter<P, I> converter) {
            this.field = field;
            this.converter = converter;
        }

        @Override
        public void set(Record target, P value, Context context) throws Exception {
            target.set(field, converter.convert(value, context));
        }
    }
}

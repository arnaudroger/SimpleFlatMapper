package org.simpleflatmapper.jooq;


import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.AbstractConstantTargetMapperBuilder;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactory;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.*;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;


public class RecordUnmapperBuilder<E> extends AbstractConstantTargetMapperBuilder<Record, E, JooqFieldKey, RecordUnmapperBuilder<E>> {


    private Field[] fields;
    private final Configuration configuration;

    public RecordUnmapperBuilder(
            ClassMeta<E> classMeta,
            MapperConfig<JooqFieldKey, ?> mapperConfig,
            Configuration configuration) {
        super(classMeta, Record.class, mapperConfig, new ConstantTargetFieldMapperFactory<Record, JooqFieldKey>() {
            @Override
            public <S, P> FieldMapper<S, Record> newFieldMapper(PropertyMapping<S, P, JooqFieldKey> propertyMapping, MappingContextFactoryBuilder contextFactoryBuilder, MapperBuilderErrorHandler mappingErrorHandler) {
                final Getter<? super S, ? extends P> getter = propertyMapping.getPropertyMeta().getGetter();
                final Field<?> field = propertyMapping.getColumnKey().getField();
                final Type propertyType = propertyMapping.getPropertyMeta().getPropertyType();
                final Class<?> fieldType = field.getType();

                if (fieldType.isAssignableFrom(TypeHelper.toClass(propertyType))) {
                    return new FieldMapper<S, Record>() {
                        @Override
                        public void mapTo(S source, Record target, MappingContext<? super S> context) throws Exception {
                            P value = getter.get(source);
                            target.set((Field<P>)field, value);
                        }
                    };
                } else {
                    final Converter converter = ConverterService.getInstance().findConverter(propertyType, fieldType);
                    if (converter == null) throw new IllegalStateException("Cannot find converter from " + propertyType + " to " + fieldType + " for field " + field);
                    return new FieldMapper<S, Record>() {
                        @Override
                        public void mapTo(S source, Record target, MappingContext<? super S> context) throws Exception {
                            P value = getter.get(source);
                            target.set((Field)field, converter.convert(value));
                        }
                    };

                }
            }
        });
        this.configuration = configuration;
    }

    @Override
    protected BiInstantiator<E, MappingContext<? super E>, Record> getInstantiator() {
        final Field[] fields = this.fields;
        final Configuration conf = this.configuration;
        return new BiInstantiator<E, MappingContext<? super E>, Record>() {
            @Override
            public Record newInstance(E e, MappingContext<? super E> mappingContext) {
                return DSL.using(conf).newRecord(fields);
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
}

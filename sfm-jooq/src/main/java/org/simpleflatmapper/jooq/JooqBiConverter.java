package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.simpleflatmapper.converter.*;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.property.ContextualConverterFactoryProperty;
import org.simpleflatmapper.map.property.GetterFactoryProperty;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.Collection;

public abstract class JooqBiConverter<F, M> implements ContextualGetter<F, M>, ContextualConverter<M, F> {

    public abstract boolean match(Type modelType);

    protected Type getRecordFieldType() {
        Type[] types = TypeHelper.getGenericParameterForClass(this.getClass(), ContextualGetter.class);
        return types != null ? types[0] : null;
    }

    protected Type getObjectType() {
        Type[] types = TypeHelper.getGenericParameterForClass(this.getClass(), ContextualGetter.class);
        return types != null ? types[1] : null;
    }

    public <R extends Record> GetterFactoryProperty buildGetterProperty() {
        ContextualGetterFactory<R, JooqFieldKey> getterFactory = new ContextualGetterFactory<R, JooqFieldKey>() {
            @Override
            public <P> ContextualGetter<R, P> newGetter(Type target, JooqFieldKey key,
                                                        MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder,
                                                        Object... properties) {
                if (!JooqBiConverter.this.match(target)) return null;
                final int index = key.getIndex();
                return new ContextualGetter<R, P>() {
                    @Override
                    public P get(R record, Context context) throws Exception {
                        return (P) JooqBiConverter.this.get((F) record.get(index), context);
                    }
                };
            }
        };

        return new GetterFactoryProperty(getterFactory);
    }

    public static ContextualConverterFactoryProperty buildConverterProperty(final Collection<JooqBiConverter> converters) {
        ContextualConverterFactory<?, ?> converterFactory =
                new AbstractContextualConverterFactory<Object, Object>(Object.class, Object.class) {
            @Override
            public ContextualConverter<Object, Object> newConverter(ConvertingTypes targetedTypes,
                                                                    ContextFactoryBuilder contextFactoryBuilder,
                                                                    Object... params) {
                for (JooqBiConverter converter : converters) {
                    if (!converter.match(targetedTypes.getFrom())) continue;
                    return converter;
                }
                return null;
            }
        };
        return ContextualConverterFactoryProperty.of(converterFactory);
    }
}

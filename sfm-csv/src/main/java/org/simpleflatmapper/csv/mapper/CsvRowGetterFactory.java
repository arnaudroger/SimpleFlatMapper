package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.csv.getter.CsvBigDecimalGetter;
import org.simpleflatmapper.csv.getter.CsvBigIntegerGetter;
import org.simpleflatmapper.csv.getter.CsvBooleanGetter;
import org.simpleflatmapper.csv.getter.CsvBoxedBooleanGetter;
import org.simpleflatmapper.csv.getter.CsvBoxedByteGetter;
import org.simpleflatmapper.csv.getter.CsvBoxedCharGetter;
import org.simpleflatmapper.csv.getter.CsvBoxedDoubleGetter;
import org.simpleflatmapper.csv.getter.CsvBoxedFloatGetter;
import org.simpleflatmapper.csv.getter.CsvBoxedIntegerGetter;
import org.simpleflatmapper.csv.getter.CsvBoxedLongGetter;
import org.simpleflatmapper.csv.getter.CsvBoxedShortGetter;
import org.simpleflatmapper.csv.getter.CsvByteGetter;
import org.simpleflatmapper.csv.getter.CsvCharGetter;
import org.simpleflatmapper.csv.getter.CsvCharSequenceGetter;
import org.simpleflatmapper.csv.getter.CsvDoubleGetter;
import org.simpleflatmapper.csv.getter.CsvFloatGetter;
import org.simpleflatmapper.csv.getter.CsvIntegerGetter;
import org.simpleflatmapper.csv.getter.CsvLongGetter;
import org.simpleflatmapper.csv.getter.CsvShortGetter;
import org.simpleflatmapper.csv.getter.CsvStringGetter;
import org.simpleflatmapper.csv.getter.CustomReaderGetter;
import org.simpleflatmapper.csv.property.CustomReaderFactoryProperty;
import org.simpleflatmapper.csv.property.CustomReaderProperty;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.*;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class CsvRowGetterFactory implements ContextualGetterFactory<CsvRow, CsvColumnKey> {
    
    public static final CsvRowGetterFactory INSTANCE = new CsvRowGetterFactory();

    private Map<Class<?>, ContextualGetterFactory<CsvRow, CsvColumnKey>> getterFactory = new HashMap<Class<?>, ContextualGetterFactory<CsvRow, CsvColumnKey>>();
    
    {
        // primitives
        getterFactory.put(boolean.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBooleanGetter(index);
            }
        });

        getterFactory.put(byte.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvByteGetter(index);
            }
        });

        getterFactory.put(char.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvCharGetter(index);
            }
        });
        
        getterFactory.put(short.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvShortGetter(index);
            }
        });

        getterFactory.put(int.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvIntegerGetter(index);
            }
        });
        
        getterFactory.put(long.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvLongGetter(index);
            }
        });

        getterFactory.put(float.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvFloatGetter(index);
            }
        });

        getterFactory.put(double.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvDoubleGetter(index);
            }
        });

        // boxed
        getterFactory.put(Boolean.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedBooleanGetter(index);
            }
        });

        getterFactory.put(Byte.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedByteGetter(index);
            }
        });

        getterFactory.put(Character.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedCharGetter(index);
            }
        });

        getterFactory.put(Short.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedShortGetter(index);
            }
        });

        getterFactory.put(Integer.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedIntegerGetter(index);
            }
        });

        getterFactory.put(Long.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedLongGetter(index);
            }
        });

        getterFactory.put(Float.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedFloatGetter(index);
            }
        });

        getterFactory.put(Double.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedDoubleGetter(index);
            }
        });
        
        getterFactory.put(String.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvStringGetter(index);
            }
        });

        getterFactory.put(CharSequence.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvCharSequenceGetter(index);
            }
        });

        getterFactory.put(BigDecimal.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBigDecimalGetter(index);
            }
        });

        getterFactory.put(BigInteger.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBigIntegerGetter(index);
            }
        });
    }
    
    @Override
    public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {

        Class<?> clazz = TypeHelper.toClass(target);

        CustomReaderProperty customReaderProperty = null;
        CustomReaderFactoryProperty customReaderFactoryProperty = null;

        for(Object o : properties) {
            if (o instanceof CustomReaderProperty) {
                customReaderProperty = (CustomReaderProperty) o;
            } else if (o instanceof CustomReaderFactoryProperty) {
                customReaderFactoryProperty = (CustomReaderFactoryProperty) o;
            }
        }

        if (customReaderProperty != null) {
            return new CustomReaderGetter<P>(key.getIndex(), customReaderProperty.getReader());
        }

        if (customReaderFactoryProperty != null) {
            CellValueReader<?> reader = customReaderFactoryProperty.getReaderFactory().getReader(target, key.getIndex(), CsvColumnDefinition.of(properties), null);
            if (reader != null) {
                return new CustomReaderGetter<P>(key.getIndex(), reader);
            }
        }
        
        if (clazz.equals(Object.class)) {
            clazz = String.class;
        }

        ContextualGetterFactory<CsvRow, CsvColumnKey> getterFactory = this.getterFactory.get(clazz);
        
        if (getterFactory != null) {
            return getterFactory.newGetter(target, key, mappingContextFactoryBuilder, properties);
        }
        

        return null;
    }


}

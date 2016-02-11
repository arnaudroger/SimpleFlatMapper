package org.sfm.map.mapper;


import org.sfm.map.FieldKey;
import org.sfm.map.FieldMapper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.column.SetterFactoryProperty;
import org.sfm.map.column.SetterProperty;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.fieldmapper.*;
import org.sfm.reflect.*;
import org.sfm.reflect.primitive.*;
import org.sfm.utils.ErrorDoc;

public class ConstantTargetFieldMapperFactorImpl<T, K extends FieldKey<K>> implements ConstantTargetFieldMapperFactory<T, K> {

    private final SetterFactory<T, PropertyMapping<?, ?, K, ? extends ColumnDefinition<K, ?>>> setterFactory;

    public ConstantTargetFieldMapperFactorImpl(SetterFactory<T, PropertyMapping<?, ?, K, ? extends ColumnDefinition<K, ?>>> setterFactory) {
        this.setterFactory = setterFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, P> FieldMapper<S, T> newFieldMapper(
            PropertyMapping<S, P, K, FieldMapperColumnDefinition<K>> pm,
            MappingContextFactoryBuilder contextFactoryBuilder,
            MapperBuilderErrorHandler mappingErrorHandler) {

        Getter<S, P> getter;

        Getter<?, ?> customGetter = pm.getColumnDefinition().getCustomGetter();
        if (customGetter != null) {
            getter = (Getter<S, P>) customGetter;
        } else {
            getter = pm.getPropertyMeta().getGetter();
        }

        Setter<T, P> setter = null;

        final SetterProperty setterProperty = pm.getColumnDefinition().lookFor(SetterProperty.class);
        if (setterProperty != null) {
            setter = (Setter<T, P>) setterProperty.getSetter();
        }

        if (setter == null) {
            final SetterFactoryProperty setterFactoryProperty = pm.getColumnDefinition().lookFor(SetterFactoryProperty.class);
            if (setterFactoryProperty != null) {
                final SetterFactory<T, PropertyMapping<?, ?, K, ? extends ColumnDefinition<K, ?>>> setterFactory =
                        (SetterFactory<T, PropertyMapping<?, ?, K, ? extends ColumnDefinition<K, ?>>>) setterFactoryProperty.getSetterFactory();
                setter = setterFactory.getSetter(pm);
            }

            if (setter == null){
                setter = setterFactory.getSetter(pm);
            }
        }

        if (TypeHelper.isPrimitive(pm.getPropertyMeta().getPropertyType())) {
            if (getter instanceof BooleanGetter && setter instanceof BooleanSetter) {
                return new BooleanFieldMapper<S, T>((BooleanGetter<S>)getter, (BooleanSetter<T>) setter);
            } else if (getter instanceof ByteGetter && setter instanceof ByteSetter) {
                return new ByteFieldMapper<S, T>((ByteGetter<S>)getter, (ByteSetter<T>) setter);
            } else if (getter instanceof CharacterGetter && setter instanceof CharacterSetter) {
                return new CharacterFieldMapper<S, T>((CharacterGetter<S>)getter, (CharacterSetter<T>) setter);
            } else if (getter instanceof ShortGetter && setter instanceof ShortSetter) {
                return new ShortFieldMapper<S, T>((ShortGetter<S>)getter, (ShortSetter<T>) setter);
            } else if (getter instanceof IntGetter && setter instanceof IntSetter) {
                return new IntFieldMapper<S, T>((IntGetter<S>)getter, (IntSetter<T>) setter);
            } else if (getter instanceof LongGetter && setter instanceof LongSetter) {
                return new LongFieldMapper<S, T>((LongGetter<S>)getter, (LongSetter<T>) setter);
            } else if (getter instanceof FloatGetter && setter instanceof FloatSetter) {
                return new FloatFieldMapper<S, T>((FloatGetter<S>)getter, (FloatSetter<T>) setter);
            } else if (getter instanceof DoubleGetter && setter instanceof DoubleSetter) {
                return new DoubleFieldMapper<S, T>((DoubleGetter<S>)getter, (DoubleSetter<T>) setter);
            }
        }

        if (getter == null) {
            mappingErrorHandler.accessorNotFound("Could not find getter for "
                    + pm + " See " + ErrorDoc.toUrl("CTFM_GETTER_NOT_FOUND"));
            return null;
        }
        if (setter == null) {
            mappingErrorHandler.accessorNotFound("Could not find setter for " + pm
                    + " See " + ErrorDoc.toUrl("STFM_GETTER_NOT_FOUND"));
            return null;
        }
        return new FieldMapperImpl<S, T, P>(getter, setter);
    }

    public static <T, K extends FieldKey<K>> ConstantTargetFieldMapperFactory<T, K> instance(
            SetterFactory<T, PropertyMapping<?, ?, K, ? extends ColumnDefinition<K, ?>>> setterFactory) {
        return new ConstantTargetFieldMapperFactorImpl<T, K>(setterFactory);
    }



}

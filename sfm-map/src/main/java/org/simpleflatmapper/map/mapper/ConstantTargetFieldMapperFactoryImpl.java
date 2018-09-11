package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.setter.*;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.primitive.*;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ObjectClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.map.fieldmapper.*;
import org.simpleflatmapper.util.ErrorDoc;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class ConstantTargetFieldMapperFactoryImpl<T, K extends FieldKey<K>> implements ConstantTargetFieldMapperFactory<T, K> {

    private final ContextualSetterFactory<T, PropertyMapping<?, ?, K>> setterFactory;
    private final Type targetType;

    private ConstantTargetFieldMapperFactoryImpl(ContextualSetterFactory<T, PropertyMapping<?, ?, K>> setterFactory, Type targetType) {
        this.setterFactory = setterFactory;
        this.targetType = targetType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, P> FieldMapper<S, T> newFieldMapper(
            PropertyMapping<S, P, K> pm,
            MappingContextFactoryBuilder contextFactoryBuilder,
            MapperBuilderErrorHandler mappingErrorHandler
            ) {

        Getter<? super S, ? extends P> getter =
               (Getter<? super S, ? extends P>) pm.getColumnDefinition().getCustomGetterFrom(pm.getPropertyMeta().getOwnerType());

        if (getter == null) {
            getter = pm.getPropertyMeta().getGetter();
        }

        if (getter == null) {
            mappingErrorHandler.accessorNotFound("Could not find getter for " + pm.getColumnKey() + " type "
                    + pm.getPropertyMeta().getPropertyType()
                    + " path " + pm.getPropertyMeta().getPath() + ", See " + ErrorDoc.CTFM_GETTER_NOT_FOUND.toUrl());
            return null;
        }

        ContextualSetter<? super T, ? super P> setter = getSetterForTarget(pm, contextFactoryBuilder);


        if (setter == null) {
            mappingErrorHandler.accessorNotFound("Could not find setter for " + pm.getColumnKey() + " type "
                    + pm.getPropertyMeta().getPropertyType()
                    + " path " + pm.getPropertyMeta().getPath() 
                    + " See " + ErrorDoc.CTFM_SETTER_NOT_FOUND.toUrl());
            return null;
        }

        Type propertyType = pm.getPropertyMeta().getPropertyType();

        return buildFieldMapper(getter, setter, propertyType);
    }

    @SuppressWarnings("unchecked")
    private <S, P> FieldMapper<S, T> buildFieldMapper(Getter<? super S, ? extends P> getter, ContextualSetter<? super T, ? super P> setter, Type propertyType) {
        if (TypeHelper.isPrimitive(propertyType)) {
            if (getter instanceof BooleanGetter && setter instanceof BooleanContextualSetter) {
                return new BooleanConstantTargetFieldMapper<S, T>((BooleanGetter<S>)getter, (BooleanContextualSetter<T>) setter);
            } else if (getter instanceof ByteGetter && setter instanceof ByteContextualSetter) {
                return new ByteConstantTargetFieldMapper<S, T>((ByteGetter<S>)getter, (ByteContextualSetter<T>) setter);
            } else if (getter instanceof CharacterGetter && setter instanceof CharacterContextualSetter) {
                return new CharacterConstantTargetFieldMapper<S, T>((CharacterGetter<S>)getter, (CharacterContextualSetter<T>) setter);
            } else if (getter instanceof ShortGetter && setter instanceof ShortContextualSetter) {
                return new ShortConstantTargetFieldMapper<S, T>((ShortGetter<S>)getter, (ShortContextualSetter<T>) setter);
            } else if (getter instanceof IntGetter && setter instanceof IntContextualSetter) {
                return new IntConstantTargetFieldMapper<S, T>((IntGetter<S>)getter, (IntContextualSetter<T>) setter);
            } else if (getter instanceof LongGetter && setter instanceof LongContextualSetter) {
                return new LongConstantTargetFieldMapper<S, T>((LongGetter<S>)getter, (LongContextualSetter<T>) setter);
            } else if (getter instanceof FloatGetter && setter instanceof FloatContextualSetter) {
                return new FloatConstantTargetFieldMapper<S, T>((FloatGetter<S>)getter, (FloatContextualSetter<T>) setter);
            } else if (getter instanceof DoubleGetter && setter instanceof DoubleContextualSetter) {
                return new DoubleConstantTargetFieldMapper<S, T>((DoubleGetter<S>)getter, (DoubleContextualSetter<T>) setter);
            }
        }

        return new ConstantTargetFieldMapper<S, T, P>(getter, setter);
    }

    @SuppressWarnings("unchecked")
    private <S, P> ContextualSetter<T, P> getSetterForTarget(PropertyMapping<S, ?, K> pm, ContextFactoryBuilder contextFactoryBuilder) {
        ContextualSetter<T, P> setter = ContextualSetterAdapter.of((Setter<T, P>) pm.getColumnDefinition().getCustomSetterTo(targetType));

        final SetterFactory<T, PropertyMapping<?, ?, K>> customSetterFactory =
                (SetterFactory<T, PropertyMapping<?, ?, K>>) pm.getColumnDefinition().getCustomSetterFactoryTo(targetType);

        if (customSetterFactory != null) {
            setter = ContextualSetterAdapter.<T, P>of(customSetterFactory.<P>getSetter(pm));
        }

        if (setter == null){
            setter = setterFactory.getSetter(pm, contextFactoryBuilder);
        }

        if (setter == null) {
            if (!pm.getPropertyMeta().isSelf()) {
                final ClassMeta<?> propertyClassMeta = pm.getPropertyMeta().getPropertyClassMeta();
                if (propertyClassMeta instanceof ObjectClassMeta) {
                    ObjectClassMeta<P> ocm = (ObjectClassMeta<P>) propertyClassMeta;

                    if (ocm.getNumberOfProperties() == 1) {
                        PropertyMeta<P, ?> subProp = ocm.getFirstProperty();

                        ContextualSetter<T, Object> subSetter = getSetterForTarget(pm.propertyMeta(subProp), contextFactoryBuilder);

                        if (subSetter != null) {
                            return new ContextualSetterOnGetter<T, Object, P>(subSetter, (Getter<P, Object>) subProp.getGetter());
                        }
                    }
                }
            }
        }
        return setter;
    }

    public static <T, K extends FieldKey<K>> ConstantTargetFieldMapperFactory<T, K> newInstance(
            SetterFactory<T, PropertyMapping<?, ?, K>> setterFactory, Type targetType) {
        return newInstance(new ContextualSetterFactoryAdapter<T, PropertyMapping<?, ?, K>>(setterFactory), targetType);
    }

    public static <T, K extends FieldKey<K>> ConstantTargetFieldMapperFactory<T, K> newInstance(
            ContextualSetterFactory<T, PropertyMapping<?, ?, K>> setterFactory, Type targetType) {
        return new ConstantTargetFieldMapperFactoryImpl<T, K>(setterFactory, targetType);
    }



}

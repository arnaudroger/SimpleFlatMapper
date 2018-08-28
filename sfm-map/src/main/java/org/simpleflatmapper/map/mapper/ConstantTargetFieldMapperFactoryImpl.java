package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.fieldmapper.*;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.setter.SetterOnGetter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ObjectClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.IntSetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;
import org.simpleflatmapper.map.fieldmapper.BooleanFieldMapper;
import org.simpleflatmapper.map.fieldmapper.ByteFieldMapper;
import org.simpleflatmapper.map.fieldmapper.CharacterFieldMapper;
import org.simpleflatmapper.map.fieldmapper.DoubleFieldMapper;
import org.simpleflatmapper.map.fieldmapper.FieldMapperImpl;
import org.simpleflatmapper.map.fieldmapper.FloatFieldMapper;
import org.simpleflatmapper.map.fieldmapper.IntFieldMapper;
import org.simpleflatmapper.map.fieldmapper.LongFieldMapper;
import org.simpleflatmapper.map.fieldmapper.ShortFieldMapper;
import org.simpleflatmapper.util.ErrorDoc;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class ConstantTargetFieldMapperFactoryImpl<T, K extends FieldKey<K>> implements ConstantTargetFieldMapperFactory<T, K> {

    private final SetterFactory<T, PropertyMapping<?, ?, K>> setterFactory;
    private final Type targetType;

    private ConstantTargetFieldMapperFactoryImpl(SetterFactory<T, PropertyMapping<?, ?, K>> setterFactory, Type targetType) {
        this.setterFactory = setterFactory;
        this.targetType = targetType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, P> FieldMapper<S, T> newFieldMapper(
            PropertyMapping<S, P, K> pm,
            MappingContextFactoryBuilder contextFactoryBuilder,
            MapperBuilderErrorHandler mappingErrorHandler) {

        FieldMapperGetter<? super S, ? extends P> getter =
                FieldMapperGetterAdapter.of((Getter<? super S, ? extends P>) pm.getColumnDefinition().getCustomGetterFrom(pm.getPropertyMeta().getOwnerType()));

        if (getter == null) {
            getter = FieldMapperGetterAdapter.of(pm.getPropertyMeta().getGetter());
        }

        if (getter == null) {
            mappingErrorHandler.accessorNotFound("Could not find getter for " + pm.getColumnKey() + " type "
                    + pm.getPropertyMeta().getPropertyType()
                    + " path " + pm.getPropertyMeta().getPath() + ", See " + ErrorDoc.CTFM_GETTER_NOT_FOUND.toUrl());
            return null;
        }

        Setter<? super T, ? super P> setter = getSetterForTarget(pm);


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
    private <S, P> FieldMapper<S, T> buildFieldMapper(FieldMapperGetter<? super S, ? extends P> getter, Setter<? super T, ? super P> setter, Type propertyType) {
        if (TypeHelper.isPrimitive(propertyType)) {
            if (getter instanceof BooleanFieldMapperGetter && setter instanceof BooleanSetter) {
                return new BooleanFieldMapper<S, T>((BooleanFieldMapperGetter<S>)getter, (BooleanSetter<T>) setter);
            } else if (getter instanceof ByteFieldMapperGetter && setter instanceof ByteSetter) {
                return new ByteFieldMapper<S, T>((ByteFieldMapperGetter<S>)getter, (ByteSetter<T>) setter);
            } else if (getter instanceof CharacterFieldMapperGetter && setter instanceof CharacterSetter) {
                return new CharacterFieldMapper<S, T>((CharacterFieldMapperGetter<S>)getter, (CharacterSetter<T>) setter);
            } else if (getter instanceof ShortFieldMapperGetter && setter instanceof ShortSetter) {
                return new ShortFieldMapper<S, T>((ShortFieldMapperGetter<S>)getter, (ShortSetter<T>) setter);
            } else if (getter instanceof IntFieldMapperGetter && setter instanceof IntSetter) {
                return new IntFieldMapper<S, T>((IntFieldMapperGetter<S>)getter, (IntSetter<T>) setter);
            } else if (getter instanceof LongFieldMapperGetter && setter instanceof LongSetter) {
                return new LongFieldMapper<S, T>((LongFieldMapperGetter<S>)getter, (LongSetter<T>) setter);
            } else if (getter instanceof FloatFieldMapperGetter && setter instanceof FloatSetter) {
                return new FloatFieldMapper<S, T>((FloatFieldMapperGetter<S>)getter, (FloatSetter<T>) setter);
            } else if (getter instanceof DoubleFieldMapperGetter && setter instanceof DoubleSetter) {
                return new DoubleFieldMapper<S, T>((DoubleFieldMapperGetter<S>)getter, (DoubleSetter<T>) setter);
            }
        }

        return new FieldMapperImpl<S, T, P>(getter, setter);
    }

    @SuppressWarnings("unchecked")
    private <S, P> Setter<T, P> getSetterForTarget(PropertyMapping<S, ?, K> pm) {
        Setter<T, P> setter = (Setter<T, P>) pm.getColumnDefinition().getCustomSetterTo(targetType);

        final SetterFactory<T, PropertyMapping<?, ?, K>> customSetterFactory =
                (SetterFactory<T, PropertyMapping<?, ?, K>>) pm.getColumnDefinition().getCustomSetterFactoryTo(targetType);

        if (customSetterFactory != null) {
            setter = customSetterFactory.getSetter(pm);
        }

        if (setter == null){
            setter = setterFactory.getSetter(pm);
        }

        if (setter == null) {
            if (!pm.getPropertyMeta().isSelf()) {
                final ClassMeta<?> propertyClassMeta = pm.getPropertyMeta().getPropertyClassMeta();
                if (propertyClassMeta instanceof ObjectClassMeta) {
                    ObjectClassMeta<P> ocm = (ObjectClassMeta<P>) propertyClassMeta;

                    if (ocm.getNumberOfProperties() == 1) {
                        PropertyMeta<P, ?> subProp = ocm.getFirstProperty();

                        Setter<T, Object> subSetter = getSetterForTarget(pm.propertyMeta(subProp));

                        if (subSetter != null) {
                            return new SetterOnGetter<T, Object, P>(subSetter, (Getter<P, Object>) subProp.getGetter());
                        }
                    }
                }
            }
        }
        return setter;
    }

    public static <T, K extends FieldKey<K>> ConstantTargetFieldMapperFactory<T, K> newInstance(
            SetterFactory<T, PropertyMapping<?, ?, K>> setterFactory, Type targetType) {
        return new ConstantTargetFieldMapperFactoryImpl<T, K>(setterFactory, targetType);
    }



}

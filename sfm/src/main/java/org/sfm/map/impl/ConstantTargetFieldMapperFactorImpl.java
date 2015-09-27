package org.sfm.map.impl;


import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.PreparedStatementSetterFactory;
import org.sfm.map.FieldMapper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.fieldmapper.*;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.*;
import org.sfm.reflect.primitive.*;

import java.lang.reflect.Type;
import java.sql.*;

public class ConstantTargetFieldMapperFactorImpl implements ConstantTargetFieldMapperFactory<PreparedStatement, JdbcColumnKey> {

    private static final ConstantTargetFieldMapperFactorImpl INSTANCE = new ConstantTargetFieldMapperFactorImpl();

    private SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>> setterFactory =
            new PreparedStatementSetterFactory();
    @Override
    public <S, P> FieldMapper<S, PreparedStatement> newFieldMapper(
            PropertyMapping<S, P, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> pm,
            MappingContextFactoryBuilder contextFactoryBuilder,
            MapperBuilderErrorHandler mappingErrorHandler) {
        FieldMapper<S, PreparedStatement> fieldMapper = null;

        Type propertyType = pm.getPropertyMeta().getPropertyType();

        Getter<S, P> getter = pm.getPropertyMeta().getGetter();

        Setter<PreparedStatement, P> setter = setterFactory.getSetter(pm);

        if (TypeHelper.isPrimitive(propertyType)) {
            if (getter instanceof BooleanGetter && setter instanceof BooleanSetter) {
                return new BooleanFieldMapper<S, PreparedStatement>((BooleanGetter<S>)getter, (BooleanSetter<PreparedStatement>) setter);
            } else if (getter instanceof ByteGetter && setter instanceof ByteSetter) {
                return new ByteFieldMapper<S, PreparedStatement>((ByteGetter<S>)getter, (ByteSetter<PreparedStatement>) setter);
            } else if (getter instanceof CharacterGetter && setter instanceof CharacterSetter) {
                return new CharacterFieldMapper<S, PreparedStatement>((CharacterGetter<S>)getter, (CharacterSetter<PreparedStatement>) setter);
            } else if (getter instanceof ShortGetter && setter instanceof ShortSetter) {
                return new ShortFieldMapper<S, PreparedStatement>((ShortGetter<S>)getter, (ShortSetter<PreparedStatement>) setter);
            } else if (getter instanceof IntGetter && setter instanceof IntSetter) {
                return new IntFieldMapper<S, PreparedStatement>((IntGetter<S>)getter, (IntSetter<PreparedStatement>) setter);
            } else if (getter instanceof LongGetter && setter instanceof LongSetter) {
                return new LongFieldMapper<S, PreparedStatement>((LongGetter<S>)getter, (LongSetter<PreparedStatement>) setter);
            } else if (getter instanceof FloatGetter && setter instanceof FloatSetter) {
                return new FloatFieldMapper<S, PreparedStatement>((FloatGetter<S>)getter, (FloatSetter<PreparedStatement>) setter);
            } else if (getter instanceof DoubleGetter && setter instanceof DoubleSetter) {
                return new DoubleFieldMapper<S, PreparedStatement>((DoubleGetter<S>)getter, (DoubleSetter<PreparedStatement>) setter);
            }
        }
        return new FieldMapperImpl<S, PreparedStatement, P>(getter, setter);
    }

    public static ConstantTargetFieldMapperFactorImpl instance() {
        return INSTANCE;
    }



}

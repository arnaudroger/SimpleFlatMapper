package org.sfm.jdbc;


import org.sfm.csv.CellWriter;
import org.sfm.csv.CsvColumnKey;
import org.sfm.jdbc.impl.setter.LongPreparedStatementSetter;
import org.sfm.jdbc.impl.setter.StringPreparedStatementSetter;
import org.sfm.map.FieldMapper;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.map.impl.fieldmapper.LongFieldMapper;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.primitive.LongGetter;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;

public class PreparedStatementFieldMapperFactory {

    private static final PreparedStatementFieldMapperFactory INSTANCE = new PreparedStatementFieldMapperFactory();

    @SuppressWarnings("unchecked")
    public <T, P> FieldMapper<T, PreparedStatement> newFieldAppender(
            PropertyMapping<T, P, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm,
            MappingContextFactoryBuilder builder) {

        Type propertyType = pm.getPropertyMeta().getPropertyType();
        Getter<T, ? extends P> getter = pm.getPropertyMeta().getGetter();

        int columnIndex = pm.getColumnKey().getIndex();
        if (TypeHelper.isClass(propertyType, String.class)) {
            return new FieldMapperImpl<T, PreparedStatement, String>((Getter<? super T, ? extends String>) getter,
                    new StringPreparedStatementSetter(columnIndex));
        } else if (TypeHelper.isClass(propertyType, Long.class) || TypeHelper.isClass(propertyType, long.class)) {
            return new LongFieldMapper<T, PreparedStatement>((LongGetter<T>) getter, new LongPreparedStatementSetter(columnIndex));
        }

        return null;
    }

    public static PreparedStatementFieldMapperFactory instance() {
        return INSTANCE;
    }
}

package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Row;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.GetterFactory;
import org.sfm.map.getter.joda.JodaTimeGetterFactory;

/*IFJAVA8_START
import org.sfm.map.getter.time.JavaTimeGetterFactory;
import java.time.*;
IFJAVA8_END*/
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class RowGetterFactory implements GetterFactory<Row, CsvColumnKey> {


    private static final Map<Class<?>, GetterFactory<Row, CsvColumnKey>> getterFactories = new HashMap<Class<?>, GetterFactory<Row, CsvColumnKey>>();
    private static final JodaTimeGetterFactory<Row, CsvColumnKey> jodaTimeGetterFactory;

    static {
        getterFactories.put(String.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiStringGetter(key.getIndex());
            }
        });
        getterFactories.put(Date.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiDateGetter(key.getIndex());
            }
        });

        getterFactories.put(Byte.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiByteGetter(key.getIndex());
            }
        });

        getterFactories.put(Character.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiCharacterGetter(key.getIndex());
            }
        });
        getterFactories.put(Short.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiShortGetter(key.getIndex());
            }
        });
        getterFactories.put(Integer.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiIntegerGetter(key.getIndex());
            }
        });
        getterFactories.put(Long.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiLongGetter(key.getIndex());
            }
        });

        getterFactories.put(Float.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiFloatGetter(key.getIndex());
            }
        });

        getterFactories.put(Double.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiDoubleGetter(key.getIndex());
            }
        });

        getterFactories.put(Boolean.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<Row, P>) new PoiBooleanGetter(key.getIndex());
            }
        });

        getterFactories.put(byte.class, getterFactories.get(Byte.class));
        getterFactories.put(char.class, getterFactories.get(Character.class));
        getterFactories.put(short.class, getterFactories.get(Short.class));
        getterFactories.put(int.class, getterFactories.get(Integer.class));
        getterFactories.put(long.class, getterFactories.get(Long.class));
        getterFactories.put(float.class, getterFactories.get(Float.class));
        getterFactories.put(double.class, getterFactories.get(Double.class));
        getterFactories.put(boolean.class, getterFactories.get(Boolean.class));


        /*IFJAVA8_START
        JavaTimeGetterFactory<Row, CsvColumnKey> javaTimeGetterFactory =
                new JavaTimeGetterFactory<Row, CsvColumnKey>(getterFactories.get(Date.class));
        getterFactories.put(LocalDate.class, javaTimeGetterFactory);
        getterFactories.put(LocalDateTime.class, javaTimeGetterFactory);
        getterFactories.put(LocalTime.class, javaTimeGetterFactory);
        getterFactories.put(OffsetDateTime.class, javaTimeGetterFactory);
        getterFactories.put(OffsetTime.class, javaTimeGetterFactory);
        getterFactories.put(ZonedDateTime.class, javaTimeGetterFactory);
        getterFactories.put(Instant.class, javaTimeGetterFactory);
        getterFactories.put(Year.class, javaTimeGetterFactory);
        getterFactories.put(YearMonth.class, javaTimeGetterFactory);
        IFJAVA8_END*/

        jodaTimeGetterFactory = new JodaTimeGetterFactory<Row, CsvColumnKey>(getterFactories.get(Date.class));

    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {

        Class<?> targetClass = TypeHelper.toClass(target);

        final GetterFactory<Row, CsvColumnKey> rowGetterFactory = getterFactories.get(targetClass);

        if (rowGetterFactory != null) {
            return rowGetterFactory.newGetter(target, key, columnDefinition);
        } else if (TypeHelper.isEnum(target)) {
            return new PoiEnumGetter(key.getIndex(), TypeHelper.toClass(target));
        } else {
            Getter<Row, P> getter = jodaTimeGetterFactory.newGetter(target, key, columnDefinition);
            if (getter != null) return getter;
        }

        return null;
    }
}

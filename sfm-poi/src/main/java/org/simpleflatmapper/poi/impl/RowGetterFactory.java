package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.csv.CsvColumnKey;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactoryRegistry;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class RowGetterFactory implements GetterFactory<Row, CsvColumnKey> {


    private static final GetterFactoryRegistry<Row, CsvColumnKey> getterFactories =
            new GetterFactoryRegistry<Row, CsvColumnKey>();

    static {
        getterFactories.put(String.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiStringGetter(key.getIndex());
            }
        });
        getterFactories.put(Date.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiDateGetter(key.getIndex());
            }
        });

        getterFactories.put(Byte.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiByteGetter(key.getIndex());
            }
        });

        getterFactories.put(Character.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiCharacterGetter(key.getIndex());
            }
        });
        getterFactories.put(Short.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiShortGetter(key.getIndex());
            }
        });
        getterFactories.put(Integer.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiIntegerGetter(key.getIndex());
            }
        });
        getterFactories.put(Long.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiLongGetter(key.getIndex());
            }
        });

        getterFactories.put(Float.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiFloatGetter(key.getIndex());
            }
        });

        getterFactories.put(Double.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiDoubleGetter(key.getIndex());
            }
        });

        getterFactories.put(Boolean.class, new GetterFactory<Row, CsvColumnKey>() {
            @Override
            public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                return (Getter<Row, P>) new PoiBooleanGetter(key.getIndex());
            }
        });

        getterFactories.mapFromTo(byte.class, Byte.class);
        getterFactories.mapFromTo(char.class, Character.class);
        getterFactories.mapFromTo(short.class, Short.class);
        getterFactories.mapFromTo(int.class, Integer.class);
        getterFactories.mapFromTo(long.class, Long.class);
        getterFactories.mapFromTo(float.class, Float.class);
        getterFactories.mapFromTo(double.class, Double.class);
        getterFactories.mapFromTo(boolean.class, Boolean.class);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
        Class<P> targetClass = TypeHelper.toClass(target);

        if (TypeHelper.isEnum(target)) {
            return new PoiEnumGetter(key.getIndex(), TypeHelper.toClass(target));
        }

        final GetterFactory<Row, CsvColumnKey> rowGetterFactory = getterFactories.findFactoryFor(targetClass);

        Getter<Row, P> getter = null;
        if (rowGetterFactory != null) {
            getter = rowGetterFactory.newGetter(target, key, properties);
        }

        if (getter != null) {
            return getter;
        }

        return null;
    }
}

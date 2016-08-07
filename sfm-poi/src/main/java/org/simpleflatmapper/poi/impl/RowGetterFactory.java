package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.csv.CsvColumnKey;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class RowGetterFactory implements GetterFactory<Row, CsvColumnKey> {


    private static final Map<Class<?>, GetterFactory<Row, CsvColumnKey>> getterFactories = new HashMap<Class<?>, GetterFactory<Row, CsvColumnKey>>();

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

        getterFactories.put(byte.class, getterFactories.get(Byte.class));
        getterFactories.put(char.class, getterFactories.get(Character.class));
        getterFactories.put(short.class, getterFactories.get(Short.class));
        getterFactories.put(int.class, getterFactories.get(Integer.class));
        getterFactories.put(long.class, getterFactories.get(Long.class));
        getterFactories.put(float.class, getterFactories.get(Float.class));
        getterFactories.put(double.class, getterFactories.get(Double.class));
        getterFactories.put(boolean.class, getterFactories.get(Boolean.class));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, Object... properties) {

        Class<?> targetClass = TypeHelper.toClass(target);

        final GetterFactory<Row, CsvColumnKey> rowGetterFactory = getterFactories.get(targetClass);

        if (rowGetterFactory != null) {
            return rowGetterFactory.newGetter(target, key, properties);
        } else if (TypeHelper.isEnum(target)) {
            return new PoiEnumGetter(key.getIndex(), TypeHelper.toClass(target));
        }

        return null;
    }
}

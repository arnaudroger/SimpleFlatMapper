package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.converter.impl.CharSequenceToEnumConverter;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CsvRowGetterFactory implements GetterFactory<CsvRow, CsvColumnKey> {
    
    public static final CsvRowGetterFactory INSTANCE = new CsvRowGetterFactory();

    private Map<Class<?>, GetterFactory<CsvRow, CsvColumnKey>> getterFactory = new HashMap<>();
    
    {
        getterFactory.put(long.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvLongGetter(index);
            }
        });

        getterFactory.put(String.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvStringGetter(index);
            }
        });

        getterFactory.put(CharSequence.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvCharSequenceGetter(index);
            }
        });
    }
    
    @Override
    public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {

        GetterFactory<CsvRow, CsvColumnKey> getterFactory = this.getterFactory.get(TypeHelper.toClass(target));
        
        if (getterFactory != null) {
            return getterFactory.newGetter(target, key, properties);
        }

        return null;
    }

    private static class CsvCharSequenceGetter implements Getter<CsvRow, CharSequence> {
        private final int index;

        public CsvCharSequenceGetter(int index) {
            this.index = index;
        }

        @Override
        public CharSequence get(CsvRow target)  {
            return target.getCharSequence(index);
        }
    }

    
    private static class CsvStringGetter implements Getter<CsvRow, String> {
        private final int index;

        public CsvStringGetter(int index) {
            this.index = index;
        }

        @Override
        public String get(CsvRow target)  {
            return target.getString(index);
        }
    }
    
    private static class CsvLongGetter implements Getter<CsvRow, Long>, LongGetter<CsvRow> {
        private final int index;

        public CsvLongGetter(int index) {
            this.index = index;
        }

        @Override
        public Long get(CsvRow target)  {
            return target.getBoxedLong(index);
        }

        @Override
        public long getLong(CsvRow target)  {
            return target.getLong(index);
        }
    }
}

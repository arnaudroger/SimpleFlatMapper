package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.converter.impl.CharSequenceToEnumConverter;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class CsvRowGetterFactory implements GetterFactory<CsvRow, CsvColumnKey> {
    
    public static final CsvRowGetterFactory INSTANCE = new CsvRowGetterFactory();

    private Map<Class<?>, GetterFactory<CsvRow, CsvColumnKey>> getterFactory = new HashMap<>();
    
    {

        getterFactory.put(Boolean.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBooleanGetter(index);
            }
        });

        getterFactory.put(Byte.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvByteGetter(index);
            }
        });

        getterFactory.put(Character.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvCharGetter(index);
            }
        });
        
        getterFactory.put(Short.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvShortGetter(index);
            }
        });

        getterFactory.put(Integer.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvIntegerGetter(index);
            }
        });
        
        getterFactory.put(Long.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvLongGetter(index);
            }
        });

        getterFactory.put(Float.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvFloatGetter(index);
            }
        });

        getterFactory.put(Double.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvDoubleGetter(index);
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

        getterFactory.put(BigDecimal.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBigDecimalGetter(index);
            }
        });

        getterFactory.put(BigInteger.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBigIntegerGetter(index);
            }
        });
    }
    
    @Override
    public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {

        Class<?> clazz = TypeHelper.toClass(target);
        
        if (clazz.isPrimitive()) {
            clazz = TypeHelper.toBoxedClass(clazz);
        }
        
        GetterFactory<CsvRow, CsvColumnKey> getterFactory = this.getterFactory.get(clazz);
        
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

    private static class CsvBigIntegerGetter implements Getter<CsvRow, BigInteger> {
        private final int index;

        public CsvBigIntegerGetter(int index) {
            this.index = index;
        }

        @Override
        public BigInteger get(CsvRow target)  {
            return target.getBigInteger(index);
        }
    }
    
    private static class CsvBigDecimalGetter implements Getter<CsvRow, BigDecimal> {
        private final int index;

        public CsvBigDecimalGetter(int index) {
            this.index = index;
        }

        @Override
        public BigDecimal get(CsvRow target)  {
            return target.getBigDecimal(index);
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

    private static class CsvByteGetter implements Getter<CsvRow, Byte>, ByteGetter<CsvRow> {
        private final int index;

        public CsvByteGetter(int index) {
            this.index = index;
        }

        @Override
        public Byte get(CsvRow target)  {
            return target.getBoxedByte(index);
        }

        @Override
        public byte getByte(CsvRow target)  {
            return target.getByte(index);
        }
    }

    private static class CsvCharGetter implements Getter<CsvRow, Character>, CharacterGetter<CsvRow> {
        private final int index;

        public CsvCharGetter(int index) {
            this.index = index;
        }

        @Override
        public Character get(CsvRow target)  {
            return target.getBoxedChar(index);
        }

        @Override
        public char getCharacter(CsvRow target)  {
            return target.getChar(index);
        }
    }

    private static class CsvShortGetter implements Getter<CsvRow, Short>, ShortGetter<CsvRow> {
        private final int index;

        public CsvShortGetter(int index) {
            this.index = index;
        }

        @Override
        public Short get(CsvRow target)  {
            return target.getBoxedShort(index);
        }

        @Override
        public short getShort(CsvRow target)  {
            return target.getShort(index);
        }
    }

    private static class CsvIntegerGetter implements Getter<CsvRow, Integer>, IntGetter<CsvRow> {
        private final int index;

        public CsvIntegerGetter(int index) {
            this.index = index;
        }

        @Override
        public Integer get(CsvRow target)  {
            return target.getBoxedInt(index);
        }

        @Override
        public int getInt(CsvRow target)  {
            return target.getInt(index);
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

    private static class CsvFloatGetter implements Getter<CsvRow, Float>, FloatGetter<CsvRow> {
        private final int index;

        public CsvFloatGetter(int index) {
            this.index = index;
        }

        @Override
        public Float get(CsvRow target)  {
            return target.getBoxedFloat(index);
        }

        @Override
        public float getFloat(CsvRow target)  {
            return target.getFloat(index);
        }
    }
    
    private static class CsvDoubleGetter implements Getter<CsvRow, Double>, DoubleGetter<CsvRow> {
        private final int index;

        public CsvDoubleGetter(int index) {
            this.index = index;
        }

        @Override
        public Double get(CsvRow target)  {
            return target.getBoxedDouble(index);
        }

        @Override
        public double getDouble(CsvRow target)  {
            return target.getDouble(index);
        }
    }

    private static class CsvBooleanGetter implements Getter<CsvRow, Boolean>, BooleanGetter<CsvRow> {
        private final int index;

        public CsvBooleanGetter(int index) {
            this.index = index;
        }

        @Override
        public Boolean get(CsvRow target)  {
            return target.getBoxedBoolean(index);
        }

        @Override
        public boolean getBoolean(CsvRow target)  {
            return target.getBoolean(index);
        }
    }
}

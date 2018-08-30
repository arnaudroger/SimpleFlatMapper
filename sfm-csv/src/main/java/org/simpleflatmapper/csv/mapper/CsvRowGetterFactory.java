package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.csv.property.CustomReaderFactoryProperty;
import org.simpleflatmapper.csv.property.CustomReaderProperty;
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

    private Map<Class<?>, GetterFactory<CsvRow, CsvColumnKey>> getterFactory = new HashMap<Class<?>, GetterFactory<CsvRow, CsvColumnKey>>();
    
    {
        // primitives
        getterFactory.put(boolean.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBooleanGetter(index);
            }
        });

        getterFactory.put(byte.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvByteGetter(index);
            }
        });

        getterFactory.put(char.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvCharGetter(index);
            }
        });
        
        getterFactory.put(short.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvShortGetter(index);
            }
        });

        getterFactory.put(int.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvIntegerGetter(index);
            }
        });
        
        getterFactory.put(long.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvLongGetter(index);
            }
        });

        getterFactory.put(float.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvFloatGetter(index);
            }
        });

        getterFactory.put(double.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvDoubleGetter(index);
            }
        });

        // boxed
        getterFactory.put(Boolean.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBoxedBooleanGetter(index);
            }
        });

        getterFactory.put(Byte.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBoxedByteGetter(index);
            }
        });

        getterFactory.put(Character.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBoxedCharGetter(index);
            }
        });

        getterFactory.put(Short.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBoxedShortGetter(index);
            }
        });

        getterFactory.put(Integer.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBoxedIntegerGetter(index);
            }
        });

        getterFactory.put(Long.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBoxedLongGetter(index);
            }
        });

        getterFactory.put(Float.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBoxedFloatGetter(index);
            }
        });

        getterFactory.put(Double.class, new GetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> Getter<CsvRow, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
                int index = key.getIndex();
                return (Getter<CsvRow, P>) new CsvBoxedDoubleGetter(index);
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

        CustomReaderProperty customReaderProperty = null;
        CustomReaderFactoryProperty customReaderFactoryProperty = null;

        for(Object o : properties) {
            if (o instanceof CustomReaderProperty) {
                customReaderProperty = (CustomReaderProperty) o;
            } else if (o instanceof CustomReaderFactoryProperty) {
                customReaderFactoryProperty = (CustomReaderFactoryProperty) o;
            }
        }

        if (customReaderProperty != null) {
            return new CustomReaderGetter<P>(key.getIndex(), customReaderProperty.getReader());
        }

        if (customReaderFactoryProperty != null) {
            CellValueReader<?> reader = customReaderFactoryProperty.getReaderFactory().getReader(target, key.getIndex(), CsvColumnDefinition.of(properties), null);
            if (reader != null) {
                return new CustomReaderGetter<P>(key.getIndex(), reader);
            }
        }
        
        if (clazz.equals(Object.class)) {
            clazz = String.class;
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
            return target.getByte(index);
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
            return target.getChar(index);
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
            return target.getShort(index);
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
            return target.getInt(index);
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
            return target.getLong(index);
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
            return target.getFloat(index);
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
            return target.getDouble(index);
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
            return target.getBoolean(index);
        }

        @Override
        public boolean getBoolean(CsvRow target)  {
            return target.getBoolean(index);
        }
    }

    private static class CsvBoxedByteGetter implements Getter<CsvRow, Byte> {
        private final int index;

        public CsvBoxedByteGetter(int index) {
            this.index = index;
        }

        @Override
        public Byte get(CsvRow target)  {
            return target.getBoxedByte(index);
        }
    }

    private static class CsvBoxedCharGetter implements Getter<CsvRow, Character> {
        private final int index;

        public CsvBoxedCharGetter(int index) {
            this.index = index;
        }

        @Override
        public Character get(CsvRow target)  {
            return target.getBoxedChar(index);
        }

    }

    private static class CsvBoxedShortGetter implements Getter<CsvRow, Short> {
        private final int index;

        public CsvBoxedShortGetter(int index) {
            this.index = index;
        }

        @Override
        public Short get(CsvRow target)  {
            return target.getBoxedShort(index);
        }

    }

    private static class CsvBoxedIntegerGetter implements Getter<CsvRow, Integer> {
        private final int index;

        public CsvBoxedIntegerGetter(int index) {
            this.index = index;
        }

        @Override
        public Integer get(CsvRow target)  {
            return target.getBoxedInt(index);
        }
    }

    private static class CsvBoxedLongGetter implements Getter<CsvRow, Long> {
        private final int index;

        public CsvBoxedLongGetter(int index) {
            this.index = index;
        }

        @Override
        public Long get(CsvRow target)  {
            return target.getBoxedLong(index);
        }
    }

    private static class CsvBoxedFloatGetter implements Getter<CsvRow, Float> {
        private final int index;

        public CsvBoxedFloatGetter(int index) {
            this.index = index;
        }

        @Override
        public Float get(CsvRow target)  {
            return target.getBoxedFloat(index);
        }
    }
    
    private static class CsvBoxedDoubleGetter implements Getter<CsvRow, Double> {
        private final int index;

        public CsvBoxedDoubleGetter(int index) {
            this.index = index;
        }

        @Override
        public Double get(CsvRow target)  {
            return target.getBoxedDouble(index);
        }
    }

    private static class CsvBoxedBooleanGetter implements Getter<CsvRow, Boolean> {
        private final int index;

        public CsvBoxedBooleanGetter(int index) {
            this.index = index;
        }

        @Override
        public Boolean get(CsvRow target)  {
            return target.getBoxedBoolean(index);
        }

    }

    private class CustomReaderGetter<P> implements Getter<CsvRow, P> {
        private final CellValueReader<?> reader;
        private final int index;

        public CustomReaderGetter(int index, CellValueReader<?> reader) {
            this.index = index;
            this.reader = reader;
        }

        @Override
        public P get(CsvRow target) throws Exception {
            return (P) target.read(reader, index);
        }
    }
}

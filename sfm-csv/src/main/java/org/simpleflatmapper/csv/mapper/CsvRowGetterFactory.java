package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.csv.property.CustomReaderFactoryProperty;
import org.simpleflatmapper.csv.property.CustomReaderProperty;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.*;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class CsvRowGetterFactory implements ContextualGetterFactory<CsvRow, CsvColumnKey> {
    
    public static final CsvRowGetterFactory INSTANCE = new CsvRowGetterFactory();

    private Map<Class<?>, ContextualGetterFactory<CsvRow, CsvColumnKey>> getterFactory = new HashMap<Class<?>, ContextualGetterFactory<CsvRow, CsvColumnKey>>();
    
    {
        // primitives
        getterFactory.put(boolean.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBooleanGetter(index);
            }
        });

        getterFactory.put(byte.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvByteGetter(index);
            }
        });

        getterFactory.put(char.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvCharGetter(index);
            }
        });
        
        getterFactory.put(short.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvShortGetter(index);
            }
        });

        getterFactory.put(int.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvIntegerGetter(index);
            }
        });
        
        getterFactory.put(long.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvLongGetter(index);
            }
        });

        getterFactory.put(float.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvFloatGetter(index);
            }
        });

        getterFactory.put(double.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvDoubleGetter(index);
            }
        });

        // boxed
        getterFactory.put(Boolean.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedBooleanGetter(index);
            }
        });

        getterFactory.put(Byte.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedByteGetter(index);
            }
        });

        getterFactory.put(Character.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedCharGetter(index);
            }
        });

        getterFactory.put(Short.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedShortGetter(index);
            }
        });

        getterFactory.put(Integer.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedIntegerGetter(index);
            }
        });

        getterFactory.put(Long.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedLongGetter(index);
            }
        });

        getterFactory.put(Float.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedFloatGetter(index);
            }
        });

        getterFactory.put(Double.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBoxedDoubleGetter(index);
            }
        });
        
        getterFactory.put(String.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvStringGetter(index);
            }
        });

        getterFactory.put(CharSequence.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvCharSequenceGetter(index);
            }
        });

        getterFactory.put(BigDecimal.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBigDecimalGetter(index);
            }
        });

        getterFactory.put(BigInteger.class, new ContextualGetterFactory<CsvRow, CsvColumnKey>() {
            @Override
            public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {
                int index = key.getIndex();
                return (ContextualGetter<CsvRow, P>) new CsvBigIntegerGetter(index);
            }
        });
    }
    
    @Override
    public <P> ContextualGetter<CsvRow, P> newGetter(final Type target, CsvColumnKey key, MappingContextFactoryBuilder<?, CsvColumnKey> mappingContextFactoryBuilder, Object... properties) {

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

        ContextualGetterFactory<CsvRow, CsvColumnKey> getterFactory = this.getterFactory.get(clazz);
        
        if (getterFactory != null) {
            return getterFactory.newGetter(target, key, mappingContextFactoryBuilder, properties);
        }
        

        return null;
    }

    public static class CsvCharSequenceGetter implements ContextualGetter<CsvRow, CharSequence>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvCharSequenceGetter(int index) {
            this.index = index;
        }

        @Override
        public CharSequence get(CsvRow target, Context context)  {
            return get(target, context, index);
        }
        
        public static CharSequence get(CsvRow target, Context context, int index) {
            return target.getCharSequence(index);
        }
    }

    public static class CsvBigIntegerGetter implements ContextualGetter<CsvRow, BigInteger>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvBigIntegerGetter(int index) {
            this.index = index;
        }

        @Override
        public BigInteger get(CsvRow target, Context context)  {
            return get(target, context, index);
        }

        public static BigInteger get(CsvRow target, Context context, int index) {
            return target.getBigInteger(index);
        }
    }

    public static class CsvBigDecimalGetter implements ContextualGetter<CsvRow, BigDecimal>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvBigDecimalGetter(int index) {
            this.index = index;
        }

        @Override
        public BigDecimal get(CsvRow target, Context context)  {
            return get(target, context, index);
        }

        public static BigDecimal get(CsvRow target, Context context, int index) {
            return target.getBigDecimal(index);
        }
    }

    public static class CsvStringGetter implements ContextualGetter<CsvRow, String>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvStringGetter(int index) {
            this.index = index;
        }

        @Override
        public String get(CsvRow target, Context context)  {
            return get(target, context, index);
        }

        public static String get(CsvRow target, Context context, int index) {
            return target.getString(index);
        }
    }
    public static class CsvByteGetter implements ContextualGetter<CsvRow, Byte>, ByteContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvByteGetter(int index) {
            this.index = index;
        }

        @Override
        public Byte get(CsvRow target, Context context)  {
            return get(target, context, index);
        }

        public static Byte get(CsvRow target, Context context, int index) {
            return target.getByte(index);
        }

        @Override
        public byte getByte(CsvRow target, Context context)  {
            return getByte(target, context, index);
        }

        public static byte getByte(CsvRow target, Context context, int index) {
            return target.getByte(index);
        }
    }

    public static class CsvCharGetter implements ContextualGetter<CsvRow, Character>, CharacterContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvCharGetter(int index) {
            this.index = index;
        }

        @Override
        public Character get(CsvRow target, Context context)  {
            return get(target, context, index);
        }

        public static Character get(CsvRow target, Context context, int index) {
            return target.getChar(index);
        }

        @Override
        public char getCharacter(CsvRow target, Context context)  {
            return getCharacter(target, context, index);
        }

        public static char getCharacter(CsvRow target, Context context, int index) {
            return target.getChar(index);
        }
    }

    public static class CsvShortGetter implements ContextualGetter<CsvRow, Short>, ShortContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvShortGetter(int index) {
            this.index = index;
        }

        @Override
        public Short get(CsvRow target, Context context)  {
            return get(target, context, index);
        }

        public static Short get(CsvRow target, Context context, int index) {
            return target.getShort(index);
        }

        @Override
        public short getShort(CsvRow target, Context context)  {
            return getShort(target, context, index);
        }

        public static short getShort(CsvRow target, Context context, int index) {
            return target.getShort(index);
        }
    }

    public static class CsvIntegerGetter implements ContextualGetter<CsvRow, Integer>, IntContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvIntegerGetter(int index) {
            this.index = index;
        }

        @Override
        public Integer get(CsvRow target, Context context)  {
            return get(target, context, index);
        }

        public static Integer get(CsvRow target, Context context, int index) {
            return target.getInt(index);
        }

        @Override
        public int getInt(CsvRow target, Context context)  {
            return getInt(target, context, index);
        }

        public static int getInt(CsvRow target, Context context, int index) {
            return target.getInt(index);
        }
    }

    public static class CsvLongGetter implements ContextualGetter<CsvRow, Long>, LongContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvLongGetter(int index) {
            this.index = index;
        }

        @Override
        public Long get(CsvRow target, Context context)  {
            return CsvLongGetter.this.get(target, context, index);
        }

        public static Long get(CsvRow target, Context context, int index) {
            return target.getLong(index);
        }

        @Override
        public long getLong(CsvRow target, Context context)  {
            return getLong(target, context, index);
        }

        public static long getLong(CsvRow target, Context context, int index) {
            return target.getLong(index);
        }
    }

    public static class CsvFloatGetter implements ContextualGetter<CsvRow, Float>, FloatContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvFloatGetter(int index) {
            this.index = index;
        }

        @Override
        public Float get(CsvRow target, Context context)  {
            return CsvFloatGetter.this.get(target, context, index);
        }

        public static Float get(CsvRow target, Context context, int index) {
            return target.getFloat(index);
        }

        @Override
        public float getFloat(CsvRow target, Context context)  {
            return getFloat(target, context, index);
        }

        public static float getFloat(CsvRow target, Context context, int index) {
            return target.getFloat(index);
        }
    }

    public static class CsvDoubleGetter implements ContextualGetter<CsvRow, Double>, DoubleContextualGetter<CsvRow>, OptimizableIndexedContextualGetter {
        public final int index;

        public CsvDoubleGetter(int index) {
            this.index = index;
        }

        @Override
        public Double get(CsvRow target, Context context)  {
            return get(target, context, index);
        }

        public static double get(CsvRow target, Context context, int index) {
            return target.getDouble(index);
        }

        @Override
        public double getDouble(CsvRow target, Context context)  {
            return getDouble(target, context, index);
        }

        public static double getDouble(CsvRow target, Context context, int index) {
            return target.getDouble(index);
        }
    }

    public static class CsvBooleanGetter implements ContextualGetter<CsvRow, Boolean>, BooleanContextualGetter<CsvRow>, OptimizableIndexedContextualGetter{
        public final int index;

        public CsvBooleanGetter(int index) {
            this.index = index;
        }

        @Override
        public Boolean get(CsvRow target, Context context)  {
            return get(target, context, index);
        }

        public static boolean get(CsvRow target, Context context, int index) {
            return target.getBoolean(index);
        }

        @Override
        public boolean getBoolean(CsvRow target, Context context)  {
            return getBoolean(target, context, index);
        }

        public static boolean getBoolean(CsvRow target, Context context, int index) {
            return target.getBoolean(index);
        }
    }

    public static class CsvBoxedByteGetter implements ContextualGetter<CsvRow, Byte> {
        public final int index;

        public CsvBoxedByteGetter(int index) {
            this.index = index;
        }

        @Override
        public Byte get(CsvRow target, Context context)  {
            return target.getBoxedByte(index);
        }
    }

    public static class CsvBoxedCharGetter implements ContextualGetter<CsvRow, Character> {
        public final int index;

        public CsvBoxedCharGetter(int index) {
            this.index = index;
        }

        @Override
        public Character get(CsvRow target, Context context)  {
            return target.getBoxedChar(index);
        }

    }

    public static class CsvBoxedShortGetter implements ContextualGetter<CsvRow, Short> {
        public final int index;

        public CsvBoxedShortGetter(int index) {
            this.index = index;
        }

        @Override
        public Short get(CsvRow target, Context context)  {
            return target.getBoxedShort(index);
        }

    }

    public static class CsvBoxedIntegerGetter implements ContextualGetter<CsvRow, Integer> {
        public final int index;

        public CsvBoxedIntegerGetter(int index) {
            this.index = index;
        }

        @Override
        public Integer get(CsvRow target, Context context)  {
            return target.getBoxedInt(index);
        }
    }

    public static class CsvBoxedLongGetter implements ContextualGetter<CsvRow, Long> {
        public final int index;

        public CsvBoxedLongGetter(int index) {
            this.index = index;
        }

        @Override
        public Long get(CsvRow target, Context context)  {
            return target.getBoxedLong(index);
        }
    }

    public static class CsvBoxedFloatGetter implements ContextualGetter<CsvRow, Float> {
        public final int index;

        public CsvBoxedFloatGetter(int index) {
            this.index = index;
        }

        @Override
        public Float get(CsvRow target, Context context)  {
            return target.getBoxedFloat(index);
        }
    }

    public static class CsvBoxedDoubleGetter implements ContextualGetter<CsvRow, Double> {
        public final int index;

        public CsvBoxedDoubleGetter(int index) {
            this.index = index;
        }

        @Override
        public Double get(CsvRow target, Context context)  {
            return target.getBoxedDouble(index);
        }
    }

    public static class CsvBoxedBooleanGetter implements ContextualGetter<CsvRow, Boolean> {
        public final int index;

        public CsvBoxedBooleanGetter(int index) {
            this.index = index;
        }

        @Override
        public Boolean get(CsvRow target, Context context)  {
            return target.getBoxedBoolean(index);
        }

    }

    private class CustomReaderGetter<P> implements ContextualGetter<CsvRow, P> {
        private final CellValueReader<?> reader;
        private final int index;

        public CustomReaderGetter(int index, CellValueReader<?> reader) {
            this.index = index;
            this.reader = reader;
        }

        @Override
        public P get(CsvRow target, Context context) throws Exception {
            return (P) target.read(reader, index);
        }
    }
}

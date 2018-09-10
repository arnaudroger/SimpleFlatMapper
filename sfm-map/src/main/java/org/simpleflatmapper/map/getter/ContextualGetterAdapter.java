package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

public final class ContextualGetterAdapter<S, P> implements ContextualGetter<S, P> {
    
    private final Getter<? super S, ? extends P> getter;

    public ContextualGetterAdapter(Getter<? super S, ? extends P> getter) {
        this.getter = getter;
    }


    @Override
    public P get(S s, Context context) throws Exception {
        return getter.get(s);
    }


    public static <S, P> ContextualGetter<S, P> of(Getter<? super S, ? extends P> getter) {
        if (getter == null) return null;

        if (getter instanceof BooleanGetter) {
            return (ContextualGetter<S, P>) new FieldMapperBooleanGetterAdapter<S>((Getter<? super S, Boolean>) getter);
        }
        if (getter instanceof ByteGetter) {
            return (ContextualGetter<S, P>) new FieldMapperByteGetterAdapter<S>((Getter<? super S, Byte>) getter);
        }
        if (getter instanceof CharacterGetter) {
            return (ContextualGetter<S, P>) new FieldMapperCharacterGetterAdapter<S>((Getter<? super S, Character>) getter);
        }
        if (getter instanceof ShortGetter) {
            return (ContextualGetter<S, P>) new FieldMapperShortGetterAdapter<S>((Getter<? super S, Short>) getter);
        }
        if (getter instanceof IntGetter) {
            return (ContextualGetter<S, P>) new FieldMapperIntGetterAdapter<S>((Getter<? super S, Integer>) getter);
        }
        if (getter instanceof LongGetter) {
            return (ContextualGetter<S, P>) new FieldMapperLongGetterAdapter<S>((Getter<? super S, Long>) getter);
        }
        if (getter instanceof FloatGetter) {
            return (ContextualGetter<S, P>) new FieldMapperFloatGetterAdapter<S>((Getter<? super S, Float>) getter);
        }
        if (getter instanceof DoubleGetter) {
            return (ContextualGetter<S, P>) new FieldMapperDoubleGetterAdapter<S>((Getter<? super S, Double>) getter);
        }
        
        return new ContextualGetterAdapter<S, P>(getter);
    }

    private static class FieldMapperBooleanGetterAdapter<S> implements ContextualGetter<S, Boolean>, BooleanContextualGetter<S> {
        private final Getter<? super S, Boolean> getter;
        private final BooleanGetter<? super S> pGetter;

        public  FieldMapperBooleanGetterAdapter(Getter<? super S, Boolean> getter) {
            this.getter = getter;
            this.pGetter = (BooleanGetter<? super S>) getter;
        }

        @Override
        public Boolean get(S s, Context context) throws Exception {
            return getter.get(s);
        }

        @Override
        public boolean getBoolean(S s, Context mappingContext) throws Exception {
            return pGetter.getBoolean(s);
        }
    }

    private static class FieldMapperByteGetterAdapter<S> implements ContextualGetter<S, Byte>, ByteContextualGetter<S> {
        private final Getter<? super S, Byte> getter;
        private final ByteGetter<? super S> pGetter;

        public  FieldMapperByteGetterAdapter(Getter<? super S, Byte> getter) {
            this.getter = getter;
            this.pGetter = (ByteGetter<? super S>) getter;
        }

        @Override
        public Byte get(S s, Context context) throws Exception {
            return getter.get(s);
        }

        @Override
        public byte getByte(S s, Context mappingContext) throws Exception {
            return pGetter.getByte(s);
        }
    }

    private static class FieldMapperCharacterGetterAdapter<S> implements ContextualGetter<S, Character>, CharacterContextualGetter<S> {
        private final Getter<? super S, Character> getter;
        private final CharacterGetter<? super S> pGetter;

        public  FieldMapperCharacterGetterAdapter(Getter<? super S, Character> getter) {
            this.getter = getter;
            this.pGetter = (CharacterGetter<? super S>) getter;
        }

        @Override
        public Character get(S s, Context context) throws Exception {
            return getter.get(s);
        }

        @Override
        public char getCharacter(S s, Context mappingContext) throws Exception {
            return pGetter.getCharacter(s);
        }
    }

    private static class FieldMapperShortGetterAdapter<S> implements ContextualGetter<S, Short>, ShortContextualGetter<S> {
        private final Getter<? super S, Short> getter;
        private final ShortGetter<? super S> pGetter;

        public  FieldMapperShortGetterAdapter(Getter<? super S, Short> getter) {
            this.getter = getter;
            this.pGetter = (ShortGetter<? super S>) getter;
        }

        @Override
        public Short get(S s, Context context) throws Exception {
            return getter.get(s);
        }

        @Override
        public short getShort(S s, Context mappingContext) throws Exception {
            return pGetter.getShort(s);
        }
    }

    private static class FieldMapperIntGetterAdapter<S> implements ContextualGetter<S, Integer>, IntContextualGetter<S> {
        private final Getter<? super S, Integer> getter;
        private final IntGetter<? super S> pGetter;

        public  FieldMapperIntGetterAdapter(Getter<? super S, Integer> getter) {
            this.getter = getter;
            this.pGetter = (IntGetter<? super S>) getter;
        }

        @Override
        public Integer get(S s, Context context) throws Exception {
            return getter.get(s);
        }

        @Override
        public int getInt(S s, Context mappingContext) throws Exception {
            return pGetter.getInt(s);
        }
    }
    private static class FieldMapperLongGetterAdapter<S> implements ContextualGetter<S, Long>, LongContextualGetter<S> {
        private final Getter<? super S, Long> getter;
        private final LongGetter<? super S> pGetter;

        public  FieldMapperLongGetterAdapter(Getter<? super S, Long> getter) {
            this.getter = getter;
            this.pGetter = (LongGetter<? super S>) getter;
        }

        @Override
        public Long get(S s, Context context) throws Exception {
            return getter.get(s);
        }

        @Override
        public long getLong(S s, Context mappingContext) throws Exception {
            return pGetter.getLong(s);
        }
    }

    private static class FieldMapperFloatGetterAdapter<S> implements ContextualGetter<S, Float>, FloatContextualGetter<S> {
        private final Getter<? super S, Float> getter;
        private final FloatGetter<? super S> pGetter;

        public  FieldMapperFloatGetterAdapter(Getter<? super S, Float> getter) {
            this.getter = getter;
            this.pGetter = (FloatGetter<? super S>) getter;
        }

        @Override
        public Float get(S s, Context context) throws Exception {
            return getter.get(s);
        }

        @Override
        public float getFloat(S s, Context mappingContext) throws Exception {
            return pGetter.getFloat(s);
        }
    }
    private static class FieldMapperDoubleGetterAdapter<S> implements ContextualGetter<S, Double>, DoubleContextualGetter<S> {
        private final Getter<? super S, Double> getter;
        private final DoubleGetter<? super S> pGetter;

        public  FieldMapperDoubleGetterAdapter(Getter<? super S, Double> getter) {
            this.getter = getter;
            this.pGetter = (DoubleGetter<? super S>) getter;
        }

        @Override
        public Double get(S s, Context context) throws Exception {
            return getter.get(s);
        }

        @Override
        public double getDouble(S s, Context mappingContext) throws Exception {
            return pGetter.getDouble(s);
        }
    }



    public static <S> BooleanContextualGetter<? super S> of(final BooleanGetter<? super S> getter) {
        return new BooleanContextualGetterAdapter<S>(getter);
    }
    
    private static class BooleanContextualGetterAdapter<S> implements BooleanContextualGetter<S> {
        private final BooleanGetter<? super S> getter;

        public BooleanContextualGetterAdapter(BooleanGetter<? super S> getter) {
            this.getter = getter;
        }

        @Override
        public boolean getBoolean(S s, Context mappingContext) throws Exception {
            return getter.getBoolean(s);
        }
    }
    public static <S> ByteContextualGetter<? super S> of(final ByteGetter<? super S> getter) {
        return new ByteContextualGetterAdapter<S>(getter);
    }

    private static class ByteContextualGetterAdapter<S> implements ByteContextualGetter<S> {
        private final ByteGetter<? super S> getter;

        public ByteContextualGetterAdapter(ByteGetter<? super S> getter) {
            this.getter = getter;
        }

        @Override
        public byte getByte(S s, Context mappingContext) throws Exception {
            return getter.getByte(s);
        }
    }
    public static <S> CharacterContextualGetter<? super S> of(final CharacterGetter<? super S> getter) {
        return new CharacterContextualGetterAdapter<S>(getter);
    }

    private static class CharacterContextualGetterAdapter<S> implements CharacterContextualGetter<S> {
        private final CharacterGetter<? super S> getter;

        public CharacterContextualGetterAdapter(CharacterGetter<? super S> getter) {
            this.getter = getter;
        }

        @Override
        public char getCharacter(S s, Context mappingContext) throws Exception {
            return getter.getCharacter(s);
        }
    }
    public static <S> ShortContextualGetter<? super S> of(final ShortGetter<? super S> getter) {
        return new ShortContextualGetterAdapter<S>(getter);
    }

    private static class ShortContextualGetterAdapter<S> implements ShortContextualGetter<S> {
        private final ShortGetter<? super S> getter;

        public ShortContextualGetterAdapter(ShortGetter<? super S> getter) {
            this.getter = getter;
        }

        @Override
        public short getShort(S s, Context mappingContext) throws Exception {
            return getter.getShort(s);
        }
    }
    public static <S> IntContextualGetter<? super S> of(final IntGetter<? super S> getter) {
        return new IntContextualGetterAdapter<S>(getter);
    }

    private static class IntContextualGetterAdapter<S> implements IntContextualGetter<S> {
        private final IntGetter<? super S> getter;

        public IntContextualGetterAdapter(IntGetter<? super S> getter) {
            this.getter = getter;
        }

        @Override
        public int getInt(S s, Context mappingContext) throws Exception {
            return getter.getInt(s);
        }
    }
    public static <S> LongContextualGetter<? super S> of(final LongGetter<? super S> getter) {
        return new LongContextualGetterAdapter<S>(getter);
    }

    private static class LongContextualGetterAdapter<S> implements LongContextualGetter<S> {
        private final LongGetter<? super S> getter;

        public LongContextualGetterAdapter(LongGetter<? super S> getter) {
            this.getter = getter;
        }

        @Override
        public long getLong(S s, Context mappingContext) throws Exception {
            return getter.getLong(s);
        }
    }
    public static <S> FloatContextualGetter<? super S> of(final FloatGetter<? super S> getter) {
        return new FloatContextualGetterAdapter<S>(getter);
    }

    private static class FloatContextualGetterAdapter<S> implements FloatContextualGetter<S> {
        private final FloatGetter<? super S> getter;

        public FloatContextualGetterAdapter(FloatGetter<? super S> getter) {
            this.getter = getter;
        }

        @Override
        public float getFloat(S s, Context mappingContext) throws Exception {
            return getter.getFloat(s);
        }
    }
    public static <S> DoubleContextualGetter<? super S> of(final DoubleGetter<? super S> getter) {
        return new DoubleContextualGetterAdapter<S>(getter);
    }

    private static class DoubleContextualGetterAdapter<S> implements DoubleContextualGetter<S> {
        private final DoubleGetter<? super S> getter;

        public DoubleContextualGetterAdapter(DoubleGetter<? super S> getter) {
            this.getter = getter;
        }

        @Override
        public double getDouble(S s, Context mappingContext) throws Exception {
            return getter.getDouble(s);
        }
    }
}

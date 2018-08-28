package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.fieldmapper.BooleanFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.ByteFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.CharacterFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.DoubleFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.FieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.FloatFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.IntFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.LongFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.ShortFieldMapperGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

public final class FieldMapperGetterAdapter<S, P> implements FieldMapperGetter<S, P> {
    
    private final Getter<? super S, ? extends P> getter;

    public FieldMapperGetterAdapter(Getter<? super S, ? extends P> getter) {
        this.getter = getter;
    }

    @Override
    public P get(S s, MappingContext<?> context) throws Exception {
        return getter.get(s);
    }


    public static <P, S> FieldMapperGetter<S, P> of(Getter<? super S, ? extends P> getter) {
        if (getter == null) return null;

        if (getter instanceof BooleanGetter) {
            return (FieldMapperGetter<S, P>) new FieldMapperBooleanGetterAdapter<S>((Getter<? super S, Boolean>) getter);
        }
        if (getter instanceof ByteGetter) {
            return (FieldMapperGetter<S, P>) new FieldMapperByteGetterAdapter<S>((Getter<? super S, Byte>) getter);
        }
        if (getter instanceof CharacterGetter) {
            return (FieldMapperGetter<S, P>) new FieldMapperCharacterGetterAdapter<S>((Getter<? super S, Character>) getter);
        }
        if (getter instanceof ShortGetter) {
            return (FieldMapperGetter<S, P>) new FieldMapperShortGetterAdapter<S>((Getter<? super S, Short>) getter);
        }
        if (getter instanceof IntGetter) {
            return (FieldMapperGetter<S, P>) new FieldMapperIntGetterAdapter<S>((Getter<? super S, Integer>) getter);
        }
        if (getter instanceof LongGetter) {
            return (FieldMapperGetter<S, P>) new FieldMapperLongGetterAdapter<S>((Getter<? super S, Long>) getter);
        }
        if (getter instanceof FloatGetter) {
            return (FieldMapperGetter<S, P>) new FieldMapperFloatGetterAdapter<S>((Getter<? super S, Float>) getter);
        }
        if (getter instanceof DoubleGetter) {
            return (FieldMapperGetter<S, P>) new FieldMapperDoubleGetterAdapter<S>((Getter<? super S, Double>) getter);
        }
        
        return new FieldMapperGetterAdapter<S, P>(getter);
    }

    private static class FieldMapperBooleanGetterAdapter<S> implements FieldMapperGetter<S, Boolean>, BooleanFieldMapperGetter<S> {
        private final Getter<? super S, Boolean> getter;
        private final BooleanGetter<? super S> pGetter;

        public  FieldMapperBooleanGetterAdapter(Getter<? super S, Boolean> getter) {
            this.getter = getter;
            this.pGetter = (BooleanGetter<? super S>) getter;
        }

        @Override
        public Boolean get(S s, MappingContext<?> context) throws Exception {
            return getter.get(s);
        }

        @Override
        public boolean getBoolean(S s, MappingContext<?> mappingContext) throws Exception {
            return pGetter.getBoolean(s);
        }
    }

    private static class FieldMapperByteGetterAdapter<S> implements FieldMapperGetter<S, Byte>, ByteFieldMapperGetter<S> {
        private final Getter<? super S, Byte> getter;
        private final ByteGetter<? super S> pGetter;

        public  FieldMapperByteGetterAdapter(Getter<? super S, Byte> getter) {
            this.getter = getter;
            this.pGetter = (ByteGetter<? super S>) getter;
        }

        @Override
        public Byte get(S s, MappingContext<?> context) throws Exception {
            return getter.get(s);
        }

        @Override
        public byte getByte(S s, MappingContext<?> mappingContext) throws Exception {
            return pGetter.getByte(s);
        }
    }

    private static class FieldMapperCharacterGetterAdapter<S> implements FieldMapperGetter<S, Character>, CharacterFieldMapperGetter<S> {
        private final Getter<? super S, Character> getter;
        private final CharacterGetter<? super S> pGetter;

        public  FieldMapperCharacterGetterAdapter(Getter<? super S, Character> getter) {
            this.getter = getter;
            this.pGetter = (CharacterGetter<? super S>) getter;
        }

        @Override
        public Character get(S s, MappingContext<?> context) throws Exception {
            return getter.get(s);
        }

        @Override
        public char getCharacter(S s, MappingContext<?> mappingContext) throws Exception {
            return pGetter.getCharacter(s);
        }
    }

    private static class FieldMapperShortGetterAdapter<S> implements FieldMapperGetter<S, Short>, ShortFieldMapperGetter<S> {
        private final Getter<? super S, Short> getter;
        private final ShortGetter<? super S> pGetter;

        public  FieldMapperShortGetterAdapter(Getter<? super S, Short> getter) {
            this.getter = getter;
            this.pGetter = (ShortGetter<? super S>) getter;
        }

        @Override
        public Short get(S s, MappingContext<?> context) throws Exception {
            return getter.get(s);
        }

        @Override
        public short getShort(S s, MappingContext<?> mappingContext) throws Exception {
            return pGetter.getShort(s);
        }
    }

    private static class FieldMapperIntGetterAdapter<S> implements FieldMapperGetter<S, Integer>, IntFieldMapperGetter<S> {
        private final Getter<? super S, Integer> getter;
        private final IntGetter<? super S> pGetter;

        public  FieldMapperIntGetterAdapter(Getter<? super S, Integer> getter) {
            this.getter = getter;
            this.pGetter = (IntGetter<? super S>) getter;
        }

        @Override
        public Integer get(S s, MappingContext<?> context) throws Exception {
            return getter.get(s);
        }

        @Override
        public int getInt(S s, MappingContext<?> mappingContext) throws Exception {
            return pGetter.getInt(s);
        }
    }
    private static class FieldMapperLongGetterAdapter<S> implements FieldMapperGetter<S, Long>, LongFieldMapperGetter<S> {
        private final Getter<? super S, Long> getter;
        private final LongGetter<? super S> pGetter;

        public  FieldMapperLongGetterAdapter(Getter<? super S, Long> getter) {
            this.getter = getter;
            this.pGetter = (LongGetter<? super S>) getter;
        }

        @Override
        public Long get(S s, MappingContext<?> context) throws Exception {
            return getter.get(s);
        }

        @Override
        public long getLong(S s, MappingContext<?> mappingContext) throws Exception {
            return pGetter.getLong(s);
        }
    }

    private static class FieldMapperFloatGetterAdapter<S> implements FieldMapperGetter<S, Float>, FloatFieldMapperGetter<S> {
        private final Getter<? super S, Float> getter;
        private final FloatGetter<? super S> pGetter;

        public  FieldMapperFloatGetterAdapter(Getter<? super S, Float> getter) {
            this.getter = getter;
            this.pGetter = (FloatGetter<? super S>) getter;
        }

        @Override
        public Float get(S s, MappingContext<?> context) throws Exception {
            return getter.get(s);
        }

        @Override
        public float getFloat(S s, MappingContext<?> mappingContext) throws Exception {
            return pGetter.getFloat(s);
        }
    }
    private static class FieldMapperDoubleGetterAdapter<S> implements FieldMapperGetter<S, Double>, DoubleFieldMapperGetter<S> {
        private final Getter<? super S, Double> getter;
        private final DoubleGetter<? super S> pGetter;

        public  FieldMapperDoubleGetterAdapter(Getter<? super S, Double> getter) {
            this.getter = getter;
            this.pGetter = (DoubleGetter<? super S>) getter;
        }

        @Override
        public Double get(S s, MappingContext<?> context) throws Exception {
            return getter.get(s);
        }

        @Override
        public double getDouble(S s, MappingContext<?> mappingContext) throws Exception {
            return pGetter.getDouble(s);
        }
    }
}

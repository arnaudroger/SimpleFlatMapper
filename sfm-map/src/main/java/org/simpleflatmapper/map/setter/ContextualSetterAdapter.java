package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;
import org.simpleflatmapper.reflect.primitive.IntSetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

public class ContextualSetterAdapter<T, P> implements ContextualSetter<T, P> {
    private final Setter<T, P> setter;

    private ContextualSetterAdapter(Setter<T, P> setter) {
        this.setter = setter;
    }

    public static <T, P> ContextualSetter<T, P> of(Setter<T, P> setter) {
        if (setter == null) return null;
        
        if (setter instanceof BooleanSetter) {
            return (ContextualSetter<T, P>) new BooleanContextualSetterAdapter<T>((Setter<T, Boolean>)setter);
        }
        if (setter instanceof ByteSetter) {
            return (ContextualSetter<T, P>) new ByteContextualSetterAdapter<T>((Setter<T, Byte>)setter);
        }
        if (setter instanceof CharacterSetter) {
            return (ContextualSetter<T, P>) new CharacterContextualSetterAdapter<T>((Setter<T, Character>)setter);
        }
        if (setter instanceof ShortSetter) {
            return (ContextualSetter<T, P>) new ShortContextualSetterAdapter<T>((Setter<T, Short>)setter);
        }
        if (setter instanceof IntSetter) {
            return (ContextualSetter<T, P>) new IntegerContextualSetterAdapter<T>((Setter<T, Integer>)setter);
        }
        if (setter instanceof LongSetter) {
            return (ContextualSetter<T, P>) new LongContextualSetterAdapter<T>((Setter<T, Long>)setter);
        }
        if (setter instanceof FloatSetter) {
            return (ContextualSetter<T, P>) new FloatContextualSetterAdapter<T>((Setter<T, Float>)setter);
        }
        if (setter instanceof DoubleSetter) {
            return (ContextualSetter<T, P>) new DoubleContextualSetterAdapter<T>((Setter<T, Double>)setter);
        }
        return new ContextualSetterAdapter<T, P>(setter);
    }
    @Override
    public void set(T target, P value, Context context) throws Exception {
        setter.set(target, value);
    }
    
    private static class BooleanContextualSetterAdapter<T> implements ContextualSetter<T, Boolean>, BooleanContextualSetter<T> {
        private final Setter<T, Boolean> setter;
        private final BooleanSetter<T> psetter;
        
        private BooleanContextualSetterAdapter(Setter<T, Boolean> setter) {
            this.setter = setter;
            this.psetter = (BooleanSetter<T>) setter;
        }


        @Override
        public void setBoolean(T target, boolean value, Context context) throws Exception {
            psetter.setBoolean(target, value);
        }

        @Override
        public void set(T target, Boolean value, Context context) throws Exception {
            setter.set(target, value);
        }
    }
    private static class ByteContextualSetterAdapter<T> implements ContextualSetter<T, Byte>, ByteContextualSetter<T> {
        private final Setter<T, Byte> setter;
        private final ByteSetter<T> psetter;

        private ByteContextualSetterAdapter(Setter<T, Byte> setter) {
            this.setter = setter;
            this.psetter = (ByteSetter<T>) setter;
        }


        @Override
        public void setByte(T target, byte value, Context context) throws Exception {
            psetter.setByte(target, value);
        }

        @Override
        public void set(T target, Byte value, Context context) throws Exception {
            setter.set(target, value);
        }
    }
    private static class CharacterContextualSetterAdapter<T> implements ContextualSetter<T, Character>, CharacterContextualSetter<T> {
        private final Setter<T, Character> setter;
        private final CharacterSetter<T> psetter;

        private CharacterContextualSetterAdapter(Setter<T, Character> setter) {
            this.setter = setter;
            this.psetter = (CharacterSetter<T>) setter;
        }


        @Override
        public void setCharacter(T target, char value, Context context) throws Exception {
            psetter.setCharacter(target, value);
        }

        @Override
        public void set(T target, Character value, Context context) throws Exception {
            setter.set(target, value);
        }
    }
    private static class ShortContextualSetterAdapter<T> implements ContextualSetter<T, Short>, ShortContextualSetter<T> {
        private final Setter<T, Short> setter;
        private final ShortSetter<T> psetter;

        private ShortContextualSetterAdapter(Setter<T, Short> setter) {
            this.setter = setter;
            this.psetter = (ShortSetter<T>) setter;
        }


        @Override
        public void setShort(T target, short value, Context context) throws Exception {
            psetter.setShort(target, value);
        }

        @Override
        public void set(T target, Short value, Context context) throws Exception {
            setter.set(target, value);
        }
    }
    private static class IntegerContextualSetterAdapter<T> implements ContextualSetter<T, Integer>, IntContextualSetter<T> {
        private final Setter<T, Integer> setter;
        private final IntSetter<T> psetter;

        private IntegerContextualSetterAdapter(Setter<T, Integer> setter) {
            this.setter = setter;
            this.psetter = (IntSetter<T>) setter;
        }


        @Override
        public void setInt(T target, int value, Context context) throws Exception {
            psetter.setInt(target, value);
        }

        @Override
        public void set(T target, Integer value, Context context) throws Exception {
            setter.set(target, value);
        }
    }
    private static class LongContextualSetterAdapter<T> implements ContextualSetter<T, Long>, LongContextualSetter<T> {
        private final Setter<T, Long> setter;
        private final LongSetter<T> psetter;

        private LongContextualSetterAdapter(Setter<T, Long> setter) {
            this.setter = setter;
            this.psetter = (LongSetter<T>) setter;
        }


        @Override
        public void setLong(T target, long value, Context context) throws Exception {
            psetter.setLong(target, value);
        }

        @Override
        public void set(T target, Long value, Context context) throws Exception {
            setter.set(target, value);
        }
    }
    private static class FloatContextualSetterAdapter<T> implements ContextualSetter<T, Float>, FloatContextualSetter<T> {
        private final Setter<T, Float> setter;
        private final FloatSetter<T> psetter;

        private FloatContextualSetterAdapter(Setter<T, Float> setter) {
            this.setter = setter;
            this.psetter = (FloatSetter<T>) setter;
        }


        @Override
        public void setFloat(T target, float value, Context context) throws Exception {
            psetter.setFloat(target, value);
        }

        @Override
        public void set(T target, Float value, Context context) throws Exception {
            setter.set(target, value);
        }
    }
    private static class DoubleContextualSetterAdapter<T> implements ContextualSetter<T, Double>, DoubleContextualSetter<T> {
        private final Setter<T, Double> setter;
        private final DoubleSetter<T> psetter;

        private DoubleContextualSetterAdapter(Setter<T, Double> setter) {
            this.setter = setter;
            this.psetter = (DoubleSetter<T>) setter;
        }


        @Override
        public void setDouble(T target, double value, Context context) throws Exception {
            psetter.setDouble(target, value);
        }

        @Override
        public void set(T target, Double value, Context context) throws Exception {
            setter.set(target, value);
        }
    }
    

    public static <T> BooleanContextualSetter<? super T> of(final BooleanSetter<? super T> setter) {
        return new PrimitiveBooleanContextualSetterAdapter<T>(setter);
    }

    private static class PrimitiveBooleanContextualSetterAdapter<T> implements BooleanContextualSetter<T> {
        private final BooleanSetter<? super T> setter;

        public PrimitiveBooleanContextualSetterAdapter(BooleanSetter<? super T> setter) {
            this.setter = setter;
        }

        @Override
        public void setBoolean(T target, boolean value, Context context) throws Exception {
            setter.setBoolean(target, value);
        }
    }

    public static <T> ByteContextualSetter<? super T> of(final ByteSetter<? super T> setter) {
        return new PrimitiveByteContextualSetterAdapter<T>(setter);
    }

    private static class PrimitiveByteContextualSetterAdapter<T> implements ByteContextualSetter<T> {
        private final ByteSetter<? super T> setter;

        public PrimitiveByteContextualSetterAdapter(ByteSetter<? super T> setter) {
            this.setter = setter;
        }

        @Override
        public void setByte(T target, byte value, Context context) throws Exception {
            setter.setByte(target, value);
        }
    }

    public static <T> ShortContextualSetter<? super T> of(final ShortSetter<? super T> setter) {
        return new PrimitiveShortContextualSetterAdapter<T>(setter);
    }

    private static class PrimitiveShortContextualSetterAdapter<T> implements ShortContextualSetter<T> {
        private final ShortSetter<? super T> setter;

        public PrimitiveShortContextualSetterAdapter(ShortSetter<? super T> setter) {
            this.setter = setter;
        }

        @Override
        public void setShort(T target, short value, Context context) throws Exception {
            setter.setShort(target, value);
        }
    }

    public static <T> IntContextualSetter<? super T> of(final IntSetter<? super T> setter) {
        return new PrimitiveIntContextualSetterAdapter<T>(setter);
    }

    private static class PrimitiveIntContextualSetterAdapter<T> implements IntContextualSetter<T> {
        private final IntSetter<? super T> setter;

        public PrimitiveIntContextualSetterAdapter(IntSetter<? super T> setter) {
            this.setter = setter;
        }

        @Override
        public void setInt(T target, int value, Context context) throws Exception {
            setter.setInt(target, value);
        }
    }


    public static <T> LongContextualSetter<? super T> of(final LongSetter<? super T> setter) {
        return new PrimitiveLongContextualSetterAdapter<T>(setter);
    }

    private static class PrimitiveLongContextualSetterAdapter<T> implements LongContextualSetter<T> {
        private final LongSetter<? super T> setter;

        public PrimitiveLongContextualSetterAdapter(LongSetter<? super T> setter) {
            this.setter = setter;
        }

        @Override
        public void setLong(T target, long value, Context context) throws Exception {
            setter.setLong(target, value);
        }
    }


    public static <T> FloatContextualSetter<? super T> of(final FloatSetter<? super T> setter) {
        return new PrimitiveFloatContextualSetterAdapter<T>(setter);
    }

    private static class PrimitiveFloatContextualSetterAdapter<T> implements FloatContextualSetter<T> {
        private final FloatSetter<? super T> setter;

        public PrimitiveFloatContextualSetterAdapter(FloatSetter<? super T> setter) {
            this.setter = setter;
        }

        @Override
        public void setFloat(T target, float value, Context context) throws Exception {
            setter.setFloat(target, value);
        }
    }


    public static <T> DoubleContextualSetter<? super T> of(final DoubleSetter<? super T> setter) {
        return new PrimitiveDoubleContextualSetterAdapter<T>(setter);
    }

    private static class PrimitiveDoubleContextualSetterAdapter<T> implements DoubleContextualSetter<T> {
        private final DoubleSetter<? super T> setter;

        public PrimitiveDoubleContextualSetterAdapter(DoubleSetter<? super T> setter) {
            this.setter = setter;
        }

        @Override
        public void setDouble(T target, double value, Context context) throws Exception {
            setter.setDouble(target, value);
        }
    }


    public static <T> CharacterContextualSetter<? super T> of(final CharacterSetter<? super T> setter) {
        return new PrimitiveCharacterContextualSetterAdapter<T>(setter);
    }

    private static class PrimitiveCharacterContextualSetterAdapter<T> implements CharacterContextualSetter<T> {
        private final CharacterSetter<? super T> setter;

        public PrimitiveCharacterContextualSetterAdapter(CharacterSetter<? super T> setter) {
            this.setter = setter;
        }

        @Override
        public void setCharacter(T target, char value, Context context) throws Exception {
            setter.setCharacter(target, value);
        }
    }
    
}

package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ErrorHelper;

public class ContextualGetterBiFunction<S, T> implements BiFunction<S, Context, T> {
    private final ContextualGetter<? super S, ? extends T> fieldMapperGetter;

    public ContextualGetterBiFunction(ContextualGetter<? super S, ? extends T> fieldMapperGetter) {
        this.fieldMapperGetter = fieldMapperGetter;
    }

    public static <S, T> BiFunction<? super S, ? super MappingContext<?>, T> of(Class<?> type, ContextualGetter<? super S, ? extends T> getter) {
        if (type.isPrimitive()) {
            if (boolean.class.equals(type) && getter instanceof BooleanContextualGetter) {
                final BooleanContextualGetter pGetter = (BooleanContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new BooleanBiFunction<S>(pGetter);
            }

            if (byte.class.equals(type) && getter instanceof ByteContextualGetter) {
                final ByteContextualGetter pGetter = (ByteContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new ByteBiFunction<S>(pGetter);
            }

            if (char.class.equals(type) && getter instanceof CharacterContextualGetter) {
                final CharacterContextualGetter pGetter = (CharacterContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new CharacterBiFunction<S>(pGetter);
            }

            if (short.class.equals(type) && getter instanceof ShortContextualGetter) {
                final ShortContextualGetter pGetter = (ShortContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new ShortBiFunction<S>(pGetter);
            }

            if (int.class.equals(type) && getter instanceof IntContextualGetter) {
                final IntContextualGetter pGetter = (IntContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new IntegerBiFunction<S>(pGetter);
            }

            if (long.class.equals(type) && getter instanceof LongContextualGetter) {
                final LongContextualGetter pGetter = (LongContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new LongBiFunction<S>(pGetter);
            }

            if (float.class.equals(type) && getter instanceof FloatContextualGetter) {
                final FloatContextualGetter pGetter = (FloatContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new FloatBiFunction<S>(pGetter);
            }

            if (double.class.equals(type) && getter instanceof DoubleContextualGetter) {
                final DoubleContextualGetter pGetter = (DoubleContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new DoubleBiFunction<S>(pGetter);
            }
            
        } 
        
        return new ContextualGetterBiFunction<S, T>(getter);
    }

    @Override
    public T apply(S s, Context mappingContext) {
        try {
            return fieldMapperGetter.get(s, mappingContext);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    public static final class BooleanBiFunction<S> implements BiFunction<S, MappingContext<?>, Boolean> {
        private final BooleanContextualGetter pGetter;

        public BooleanBiFunction(BooleanContextualGetter pGetter) {
            this.pGetter = pGetter;
        }

        @Override
        public Boolean apply(S s, MappingContext<?> mappingContext) {
            try {
                return pGetter.getBoolean(s, mappingContext);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }

    public static final class ByteBiFunction<S> implements BiFunction<S, MappingContext<?>, Byte> {
        private final ByteContextualGetter pGetter;

        public ByteBiFunction(ByteContextualGetter pGetter) {
            this.pGetter = pGetter;
        }

        @Override
        public Byte apply(S s, MappingContext<?> mappingContext) {
            try {
                return pGetter.getByte(s, mappingContext);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }

    public static final class CharacterBiFunction<S> implements BiFunction<S, MappingContext<?>, Character> {
        private final CharacterContextualGetter pGetter;

        public CharacterBiFunction(CharacterContextualGetter pGetter) {
            this.pGetter = pGetter;
        }

        @Override
        public Character apply(S s, MappingContext<?> mappingContext) {
            try {
                return pGetter.getCharacter(s, mappingContext);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }

    public static final class ShortBiFunction<S> implements BiFunction<S, MappingContext<?>, Short> {
        private final ShortContextualGetter pGetter;

        public ShortBiFunction(ShortContextualGetter pGetter) {
            this.pGetter = pGetter;
        }

        @Override
        public Short apply(S s, MappingContext<?> mappingContext) {
            try {
                return pGetter.getShort(s, mappingContext);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }

    public static final class IntegerBiFunction<S> implements BiFunction<S, MappingContext<?>, Integer> {
        private final IntContextualGetter pGetter;

        public IntegerBiFunction(IntContextualGetter pGetter) {
            this.pGetter = pGetter;
        }

        @Override
        public Integer apply(S s, MappingContext<?> mappingContext) {
            try {
                return pGetter.getInt(s, mappingContext);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }

    public static final class LongBiFunction<S> implements BiFunction<S, MappingContext<?>, Long> {
        private final LongContextualGetter pGetter;

        public LongBiFunction(LongContextualGetter pGetter) {
            this.pGetter = pGetter;
        }

        @Override
        public Long apply(S s, MappingContext<?> mappingContext) {
            try {
                return pGetter.getLong(s, mappingContext);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }

    public static final class FloatBiFunction<S> implements BiFunction<S, MappingContext<?>, Float> {
        private final FloatContextualGetter pGetter;

        public FloatBiFunction(FloatContextualGetter pGetter) {
            this.pGetter = pGetter;
        }

        @Override
        public Float apply(S s, MappingContext<?> mappingContext) {
            try {
                return pGetter.getFloat(s, mappingContext);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }

    public static final class DoubleBiFunction<S> implements BiFunction<S, MappingContext<?>, Double> {
        private final DoubleContextualGetter pGetter;

        public DoubleBiFunction(DoubleContextualGetter pGetter) {
            this.pGetter = pGetter;
        }

        @Override
        public Double apply(S s, MappingContext<?> mappingContext) {
            try {
                return pGetter.getDouble(s, mappingContext);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }
}

package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetter;
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
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new BiFunction<S, MappingContext<?>, Boolean>() {
                    @Override
                    public Boolean apply(S s, MappingContext<?> mappingContext) {
                        try {
                            return pGetter.getBoolean(s, mappingContext);
                        } catch (Exception e) {
                            return ErrorHelper.rethrow(e);
                        }
                    }
                };
            }

            if (byte.class.equals(type) && getter instanceof ByteContextualGetter) {
                final ByteContextualGetter pGetter = (ByteContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new BiFunction<S, MappingContext<?>, Byte>() {
                    @Override
                    public Byte apply(S s, MappingContext<?> mappingContext) {
                        try {
                            return pGetter.getByte(s, mappingContext);
                        } catch (Exception e) {
                            return ErrorHelper.rethrow(e);
                        }
                    }
                };
            }

            if (char.class.equals(type) && getter instanceof CharacterContextualGetter) {
                final CharacterContextualGetter pGetter = (CharacterContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new BiFunction<S, MappingContext<?>, Character>() {
                    @Override
                    public Character apply(S s, MappingContext<?> mappingContext) {
                        try {
                            return pGetter.getCharacter(s, mappingContext);
                        } catch (Exception e) {
                            return ErrorHelper.rethrow(e);
                        }
                    }
                };
            }

            if (short.class.equals(type) && getter instanceof ShortContextualGetter) {
                final ShortContextualGetter pGetter = (ShortContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new BiFunction<S, MappingContext<?>, Short>() {
                    @Override
                    public Short apply(S s, MappingContext<?> mappingContext) {
                        try {
                            return pGetter.getShort(s, mappingContext);
                        } catch (Exception e) {
                            return ErrorHelper.rethrow(e);
                        }
                    }
                };
            }

            if (int.class.equals(type) && getter instanceof IntContextualGetter) {
                final IntContextualGetter pGetter = (IntContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new BiFunction<S, MappingContext<?>, Integer>() {
                    @Override
                    public Integer apply(S s, MappingContext<?> mappingContext) {
                        try {
                            return pGetter.getInt(s, mappingContext);
                        } catch (Exception e) {
                            return ErrorHelper.rethrow(e);
                        }
                    }
                };
            }

            if (long.class.equals(type) && getter instanceof LongContextualGetter) {
                final LongContextualGetter pGetter = (LongContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new BiFunction<S, MappingContext<?>, Long>() {
                    @Override
                    public Long apply(S s, MappingContext<?> mappingContext) {
                        try {
                            return pGetter.getLong(s, mappingContext);
                        } catch (Exception e) {
                            return ErrorHelper.rethrow(e);
                        }
                    }
                };
            }

            if (float.class.equals(type) && getter instanceof FloatContextualGetter) {
                final FloatContextualGetter pGetter = (FloatContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new BiFunction<S, MappingContext<?>, Float>() {
                    @Override
                    public Float apply(S s, MappingContext<?> mappingContext) {
                        try {
                            return pGetter.getFloat(s, mappingContext);
                        } catch (Exception e) {
                            return ErrorHelper.rethrow(e);
                        }
                    }
                };
            }

            if (double.class.equals(type) && getter instanceof DoubleContextualGetter) {
                final DoubleContextualGetter pGetter = (DoubleContextualGetter) getter;
                return (BiFunction<? super S, ? super MappingContext<?>, T>) new BiFunction<S, MappingContext<?>, Double>() {
                    @Override
                    public Double apply(S s, MappingContext<?> mappingContext) {
                        try {
                            return pGetter.getDouble(s, mappingContext);
                        } catch (Exception e) {
                            return ErrorHelper.rethrow(e);
                        }
                    }
                };
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
}

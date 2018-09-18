package org.simpleflatmapper.map.property;

import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ContextualConverterAdapter;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class ConverterProperty<I, O> {

    public final ContextualConverter<I, O> function;
    public final Type inType;

    public ConverterProperty(Converter<I, O> function, Type inType) {
        this.function = new ContextualConverterAdapter<I, O>(function);
        this.inType = inType;
    }

    public ConverterProperty(ContextualConverter<I, O> function, Type inType) {
        this.function = function;
        this.inType = inType;
    }

    public static <I, O> ConverterProperty of(ContextualConverter<I, O> converter, Type inType) {
        return new ConverterProperty<I, O>(converter, inType);
    }

    public static <I, O> ConverterProperty of(ContextualConverter<I, O> converter) {
        return new ConverterProperty<I, O>(converter, getInType(converter));
    }

    private static <O, I> Type getInType(ContextualConverter<I, O> converter) {
        Type[] types = TypeHelper.getGenericParameterForClass(converter.getClass(), ContextualConverter.class);
        if ( types == null) {
            return Object.class;
        } else {
            return types[0];
        }
    }
}

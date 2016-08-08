package org.simpleflatmapper.converter;

import java.io.IOException;

public interface AppenderConverter<I, O extends CharSequence> extends Converter<I, O> {
    void appendTo(I in, Appendable appendable) throws IOException;
}

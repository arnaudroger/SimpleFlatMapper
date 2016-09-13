package org.simpleflatmapper.util;

public interface Consumer<T> extends CheckedConsumer<T>
//IFJAVA8_START
    , java.util.function.Consumer<T>
//IFJAVA8_END
{
    void accept(T t);
}

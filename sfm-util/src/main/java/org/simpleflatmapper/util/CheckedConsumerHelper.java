package org.simpleflatmapper.util;

public class CheckedConsumerHelper {
    public static <T> Consumer<T> toConsumer(final CheckedConsumer<T> consumer) {
        return new Consumer<T>() {
            @Override
            public void accept(T t) {
                try {
                    consumer.accept(t);
                } catch (Exception e) {
                    ErrorHelper.rethrow(e);
                }
            }
        };
    }
}

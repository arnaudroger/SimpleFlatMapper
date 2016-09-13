package org.simpleflatmapper.util;


public interface CheckedConsumer<T> {
    void accept(T t) throws Exception;

    static <T> Consumer<T> toConsumer(final CheckedConsumer<? super T> cc) {
        return new Consumer<T>() {
            @Override
            public void accept(T t) {
                try {
                    cc.accept(t);
                } catch (Exception e) {
                    ErrorHelper.rethrow(e);
                }
            }
        };
    }
}

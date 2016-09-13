package org.simpleflatmapper.util;


public interface CheckedConsumer<T> {
    void accept(T t) throws Exception;

    //IFJAVA8_START
    default Consumer<T> toConsumer() {
        return t -> {
            try {
                accept(t);
            } catch (Exception e) {
                ErrorHelper.rethrow(e);
            }
        };
    }

    static <T> Consumer<T> toConsumer(CheckedConsumer<T> consumer) {
        return consumer.toConsumer();
    }
    //IFJAVA8_END

}

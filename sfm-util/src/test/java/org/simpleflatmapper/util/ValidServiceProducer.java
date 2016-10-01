package org.simpleflatmapper.util;

public class ValidServiceProducer implements ServiceProducer {
    @Override
    public void produce(Consumer<? super String> consumer) {
        consumer.accept("ValidServiceProducer");
    }
}

package org.simpleflatmapper.util.test;

import org.simpleflatmapper.util.Consumer;

public class ValidServiceProducer implements ServiceProducer {
    @Override
    public void produce(Consumer<? super String> consumer) {
        consumer.accept("ValidServiceProducer");
    }
}

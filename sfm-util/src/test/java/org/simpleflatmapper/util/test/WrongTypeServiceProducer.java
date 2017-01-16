package org.simpleflatmapper.util.test;

import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ProducerServiceLoader;

public class WrongTypeServiceProducer implements ProducerServiceLoader.Producer<String> {
    @Override
    public void produce(Consumer<? super String> consumer) {
        consumer.accept("WrongType");
    }
}

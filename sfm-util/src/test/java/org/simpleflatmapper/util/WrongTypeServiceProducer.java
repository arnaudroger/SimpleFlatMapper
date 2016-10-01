package org.simpleflatmapper.util;

public class WrongTypeServiceProducer implements ProducerServiceLoader.Producer<String>{
    @Override
    public void produce(Consumer<? super String> consumer) {
        consumer.accept("WrongType");
    }
}

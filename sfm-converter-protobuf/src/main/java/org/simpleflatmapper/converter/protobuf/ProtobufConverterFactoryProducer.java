package org.simpleflatmapper.converter.protobuf;

import com.google.protobuf.Timestamp;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.ConverterFactory;
import org.simpleflatmapper.util.Consumer;

import java.util.Date;

public class ProtobufConverterFactoryProducer extends AbstractConverterFactoryProducer {

    @Override
    public void produce(Consumer<? super ConverterFactory<?, ?>> consumer) {
        constantConverter(consumer, Date.class, Timestamp.class, new DateToPTimestampConverter());
        constantConverter(consumer, Long.class, Timestamp.class, new LongToPTimestampConverter());
    }
}

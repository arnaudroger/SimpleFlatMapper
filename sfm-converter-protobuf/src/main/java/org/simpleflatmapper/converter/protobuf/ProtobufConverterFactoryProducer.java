package org.simpleflatmapper.converter.protobuf;

import com.google.protobuf.Timestamp;
import org.simpleflatmapper.converter.AbstractContextualConverterFactoryProducer;
import org.simpleflatmapper.converter.ContextualConverterFactory;
import org.simpleflatmapper.util.Consumer;

import java.util.Date;

public class ProtobufConverterFactoryProducer extends AbstractContextualConverterFactoryProducer {

    @Override
    public void produce(Consumer<? super ContextualConverterFactory<?, ?>> consumer) {
        constantConverter(consumer, Date.class, Timestamp.class, new DateToPTimestampConverter());
        constantConverter(consumer, Long.class, Timestamp.class, new LongToPTimestampConverter());
    }
}

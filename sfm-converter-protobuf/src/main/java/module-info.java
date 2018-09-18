import org.simpleflatmapper.converter.ContextualConverterFactoryProducer;

module org.simpleflatmapper.converter.protobuf {
        requires org.simpleflatmapper.util;
        requires org.simpleflatmapper.converter;
        requires protobuf.java;
        exports org.simpleflatmapper.converter.protobuf;

        provides ContextualConverterFactoryProducer
            with org.simpleflatmapper.converter.protobuf.ProtobufConverterFactoryProducer;
}
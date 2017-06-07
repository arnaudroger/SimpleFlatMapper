module simpleflatmapper.converter.protobuf {
        requires simpleflatmapper.util;
        requires simpleflatmapper.converter;
        requires protobuf.java;
        exports org.simpleflatmapper.converter.protobuf;

        provides org.simpleflatmapper.converter.ConverterFactoryProducer
            with org.simpleflatmapper.converter.protobuf.ProtobufConverterFactoryProducer;
}
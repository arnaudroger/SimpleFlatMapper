import org.simpleflatmapper.converter.ConverterFactoryProducer;

module org.simpleflatmapper.converter.test {
        requires org.simpleflatmapper.converter;
        requires org.simpleflatmapper.util;
        requires junit;
        exports org.simpleflatmapper.converter.test;

        provides ConverterFactoryProducer
                with org.simpleflatmapper.converter.test.FooConverterFactoryProducer;
}
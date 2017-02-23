module simpleflatmapper.converter.joda {
        requires simpleflatmapper.util;
        requires simpleflatmapper.converter;
        requires joda.time;
        exports org.simpleflatmapper.converter.joda;

        provides org.simpleflatmapper.converter.ConverterFactoryProducer
            with org.simpleflatmapper.converter.joda.JodaTimeConverterFactoryProducer;
}
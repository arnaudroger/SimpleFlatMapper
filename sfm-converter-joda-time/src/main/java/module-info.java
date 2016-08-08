module org.simpleflatmapper.converter.joda {
        requires org.simpleflatmapper.util;
        requires org.simpleflatmapper.converter;
        requires joda.time;
        exports org.simpleflatmapper.converter.joda;

        provides org.simpleflatmapper.converter.ConverterFactoryProducer
            with org.simpleflatmapper.converter.joda.JodaTimeConverterFactoryProducer;
}
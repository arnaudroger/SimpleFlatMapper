import org.simpleflatmapper.converter.ContextualConverterFactoryProducer;

module org.simpleflatmapper.converter.joda {
        requires org.simpleflatmapper.util;
        requires org.simpleflatmapper.converter;
        requires org.joda.time;
        exports org.simpleflatmapper.converter.joda;

        provides ContextualConverterFactoryProducer
            with org.simpleflatmapper.converter.joda.JodaTimeConverterFactoryProducer;
}
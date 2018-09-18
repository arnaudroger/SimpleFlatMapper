import org.simpleflatmapper.converter.ContextualConverterFactoryProducer;
import org.simpleflatmapper.converter.ConverterFactoryProducer;

module org.simpleflatmapper.converter {
        requires org.simpleflatmapper.util;
        exports org.simpleflatmapper.converter;

        uses ContextualConverterFactoryProducer;
        uses ConverterFactoryProducer;

}
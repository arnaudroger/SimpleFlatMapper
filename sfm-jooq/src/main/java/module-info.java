import org.simpleflatmapper.converter.ContextualConverterFactoryProducer;

module org.simpleflatmapper.jooq {
        requires transitive org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires transitive org.jooq;
        requires java.sql;
        exports org.simpleflatmapper.jooq;

        provides ContextualConverterFactoryProducer
                with org.simpleflatmapper.jooq.converter.JooqConverterFactoryProducer;

}
module org.simpleflatmapper.jooq {
        requires transitive org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires transitive jooq;
        requires java.sql;
        exports org.simpleflatmapper.jooq;

        provides org.simpleflatmapper.converter.ConverterFactoryProducer
                with org.simpleflatmapper.jooq.converter.JooqConverterFactoryProducer;

}
module org.simpleflatmapper.jdbc {
    requires public org.simpleflatmapper.map;

    requires java.sql;

    exports org.simpleflatmapper.jdbc;
    exports org.simpleflatmapper.jdbc.named;

    provides org.simpleflatmapper.converter.ConverterFactoryProducer
        with org.simpleflatmapper.jdbc.converter.JdbcConverterFactoryProducer;
    provides org.simpleflatmapper.reflect.meta.AliasProviderFactory
        with org.simpleflatmapper.jdbc.impl.JpaAliasProviderFactory;
}
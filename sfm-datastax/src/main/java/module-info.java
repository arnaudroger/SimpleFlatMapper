module org.simpleflatmapper.datastax {
        requires transitive org.simpleflatmapper.map;
        requires transitive org.simpleflatmapper.tuple;
        requires cassandra.driver.core;
        requires guava;
        exports org.simpleflatmapper.datastax;

        provides org.simpleflatmapper.reflect.meta.AliasProviderProducer
        with org.simpleflatmapper.datastax.impl.mapping.DatastaxAliasProviderFactory;
}
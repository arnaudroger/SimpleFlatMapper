module simpleflatmapper.datastax {
        requires transitive simpleflatmapper.map;
        requires transitive simpleflatmapper.tuple;
        requires cassandra.driver.core;
        requires guava;
        exports org.simpleflatmapper.datastax;

        provides org.simpleflatmapper.reflect.meta.AliasProviderProducer
        with org.simpleflatmapper.datastax.impl.mapping.DatastaxAliasProviderFactory;
}
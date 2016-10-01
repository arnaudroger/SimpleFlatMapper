module org.simpleflatmapper.datastax {
        requires org.simpleflatmapper.map;
        requires org.simpleflatmapper.tuple;
        requires cassandra.driver.core;
        requires guava;
        exports org.simpleflatmapper.datastax;

        provides org.simpleflatmapper.reflect.meta.AliasProviderProducer
        with org.simpleflatmapper.datastax.impl.mapping.DatastaxAliasProviderFactory;
}
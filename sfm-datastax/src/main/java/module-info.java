module org.simpleflatmapper.datastax {
        requires org.simpleflatmapper.map;
        requires cassandra.driver.core;
        requires cassandra.driver.mapping;
        requires guava;
        exports org.simpleflatmapper.datastax;
}
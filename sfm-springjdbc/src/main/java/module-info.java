module org.simpleflatmapper.jdbc.spring {
        requires transitive org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires transitive spring.jdbc;
        requires transitive spring.tx;
        requires transitive spring.beans;
        requires transitive spring.core;
        requires transitive java.sql;

        exports org.simpleflatmapper.jdbc.spring;
}
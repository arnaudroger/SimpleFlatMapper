module simpleflatmapper.jdbc.spring {
        requires transitive simpleflatmapper.map;
        requires simpleflatmapper.jdbc;
        requires transitive spring.jdbc;
        requires transitive spring.tx;
        requires transitive spring.beans;
        requires transitive spring.core;
        requires transitive java.sql;

        exports org.simpleflatmapper.jdbc.spring;
}
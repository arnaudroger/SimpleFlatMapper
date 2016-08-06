module org.simpleflatmapper.jdbc.spring {
        requires org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires spring.jdbc;
        requires spring.tx;
        requires spring.beans;
        requires spring.core;
        requires java.sql;

        exports org.simpleflatmapper.jdbc.spring;
}
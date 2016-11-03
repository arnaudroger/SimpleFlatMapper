module org.simpleflatmapper.jdbc.spring {
        requires public org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires public spring.jdbc;
        requires public spring.tx;
        requires public spring.beans;
        requires public spring.core;
        requires public java.sql;

        exports org.simpleflatmapper.jdbc.spring;
}
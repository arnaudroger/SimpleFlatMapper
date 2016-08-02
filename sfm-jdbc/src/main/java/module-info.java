module org.simpleflatmapper.jdbc {
    requires org.simpleflatmapper.core;
    requires org.objectweb.asm;
    requires joda.time;
    requires java.sql;
    requires javax.persistence;
    requires mysql.connector.java;
    exports org.simpleflatmapper.jdbc;
}
module org.simpleflatmapper.jdbc {
    requires org.simpleflatmapper.map;
    requires java.sql;
    requires javax.persistence;
    requires mysql.connector.java;
    requires jada.time;
    exports org.simpleflatmapper.jdbc;
    exports org.simpleflatmapper.jdbc.named;
}
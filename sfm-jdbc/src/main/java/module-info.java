module org.simpleflatmapper.jdbc {
    requires org.simpleflatmapper.map;
    requires java.sql;
    requires javax.persistence;
    requires mysql.connector.java;
    exports org.simpleflatmapper.jdbc;
}
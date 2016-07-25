module org.sfm {
    requires java.logging;
    requires java.sql;
    requires org.objectweb.asm;
    requires joda.time;
    requires javax.persistence;
    requires mysql.connector.java;
    exports org.sfm.csv;
    exports org.sfm.jdbc;
    exports org.sfm.map;
}
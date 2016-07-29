module org.sfm.core {
    requires org.objectweb.asm;
    requires joda.time;
    requires javax.persistence;
    requires mysql.connector.java;
    requires java.sql;
    exports org.sfm.map;
    exports org.sfm.reflect;
    exports org.sfm.reflect.asm;
}
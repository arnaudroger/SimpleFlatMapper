module org.sfm.core {
    requires org.objectweb.asm;
    requires joda.time;
    requires java.logging;
    exports org.sfm.map;
    exports org.sfm.reflect;
    exports org.sfm.reflect.asm;
}
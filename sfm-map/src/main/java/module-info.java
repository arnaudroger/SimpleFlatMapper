module org.simpleflatmapper.map {
        requires org.objectweb.asm;
        requires java.logging;

        requires public org.simpleflatmapper.util;
        requires public org.simpleflatmapper.reflect;
        requires public org.simpleflatmapper.converter;

        exports org.simpleflatmapper.map;
        exports org.simpleflatmapper.map.context;
        exports org.simpleflatmapper.map.mapper;
        exports org.simpleflatmapper.map.fieldmapper;
        exports org.simpleflatmapper.map.column;
        exports org.simpleflatmapper.map.error;
        exports org.simpleflatmapper.map.column.joda;
        exports org.simpleflatmapper.map.column.time;
}
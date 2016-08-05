module org.simpleflatmapper.map {
        requires org.objectweb.asm;
        requires java.logging;
        requires joda.time;

        requires public org.simpleflatmapper.util;
        requires public org.simpleflatmapper.reflect;
        requires public org.simpleflatmapper.converter;

        exports org.simpleflatmapper.core.map;
        exports org.simpleflatmapper.core.map.context;
        exports org.simpleflatmapper.core.map.mapper;
        exports org.simpleflatmapper.core.map.fieldmapper;
        exports org.simpleflatmapper.core.map.column;
        exports org.simpleflatmapper.core.map.error;
        exports org.simpleflatmapper.core.map.column.joda;
        exports org.simpleflatmapper.core.map.column.time;
}
module org.simpleflatmapper.core {
        requires org.objectweb.asm;
        requires joda.time;
        requires java.logging;

        exports org.simpleflatmapper.core.map;
        exports org.simpleflatmapper.core.map.context;
        exports org.simpleflatmapper.core.map.mapper;
        exports org.simpleflatmapper.core.map.fieldmapper;
        exports org.simpleflatmapper.core.map.column;
        exports org.simpleflatmapper.core.map.error;
        exports org.simpleflatmapper.core.map.column.joda;
        exports org.simpleflatmapper.core.map.column.time;
        exports org.simpleflatmapper.core.reflect;
        exports org.simpleflatmapper.core.reflect.meta;
        exports org.simpleflatmapper.core.reflect.asm;
        exports org.simpleflatmapper.core.reflect.getter;
        exports org.simpleflatmapper.core.reflect.getter.joda;
        exports org.simpleflatmapper.core.reflect.getter.time;
        exports org.simpleflatmapper.core.reflect.setter;
        exports org.simpleflatmapper.core.reflect.primitive;
        exports org.simpleflatmapper.core.tuples;
        exports org.simpleflatmapper.core.utils;
        exports org.simpleflatmapper.core.conv;
}
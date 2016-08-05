module org.simpleflatmapper.reflect {
        requires org.objectweb.asm;
        requires joda.time;
        requires org.simpleflatmapper.util;
        requires org.simpleflatmapper.converter;

        exports org.simpleflatmapper.core.reflect;
        exports org.simpleflatmapper.core.reflect.meta;
        exports org.simpleflatmapper.core.reflect.asm;
        exports org.simpleflatmapper.core.reflect.getter;
        exports org.simpleflatmapper.core.reflect.getter.joda;
        exports org.simpleflatmapper.core.reflect.getter.time;
        exports org.simpleflatmapper.core.reflect.setter;
        exports org.simpleflatmapper.core.reflect.primitive;
}
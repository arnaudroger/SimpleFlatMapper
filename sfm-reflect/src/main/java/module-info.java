module org.simpleflatmapper.reflect {
        requires org.objectweb.asm;
        requires org.simpleflatmapper.util;
        requires org.simpleflatmapper.converter;

        exports org.simpleflatmapper.reflect;
        exports org.simpleflatmapper.reflect.meta;
        exports org.simpleflatmapper.reflect.asm;
        exports org.simpleflatmapper.reflect.getter;
        exports org.simpleflatmapper.reflect.getter.joda;
        exports org.simpleflatmapper.reflect.getter.time;
        exports org.simpleflatmapper.reflect.setter;
        exports org.simpleflatmapper.reflect.primitive;
}
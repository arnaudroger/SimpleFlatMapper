module org.simpleflatmapper.reflect {
        requires public org.objectweb.asm;
        requires public org.simpleflatmapper.util;
        requires public org.simpleflatmapper.converter;

        exports org.simpleflatmapper.reflect;
        exports org.simpleflatmapper.reflect.meta;
        exports org.simpleflatmapper.reflect.asm;
        exports org.simpleflatmapper.reflect.getter;
        exports org.simpleflatmapper.reflect.setter;
        exports org.simpleflatmapper.reflect.primitive;

        uses org.simpleflatmapper.reflect.meta.AliasProviderFactory;
}
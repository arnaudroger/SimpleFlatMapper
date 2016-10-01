module org.simpleflatmapper.reflect {
        requires public org.simpleflatmapper.util;
        requires public org.simpleflatmapper.converter;

        exports org.simpleflatmapper.reflect;
        exports org.simpleflatmapper.reflect.meta;
        exports org.simpleflatmapper.reflect.asm;
        exports org.simpleflatmapper.reflect.instantiator;
        exports org.simpleflatmapper.reflect.getter;
        exports org.simpleflatmapper.reflect.setter;
        exports org.simpleflatmapper.reflect.primitive;
        exports org.simpleflatmapper.ow2asm;
        exports org.simpleflatmapper.ow2asm.signature;

        uses org.simpleflatmapper.reflect.meta.AliasProviderProducer;
        uses org.simpleflatmapper.reflect.ReflectionService.ClassMetaFactoryProducer;
}
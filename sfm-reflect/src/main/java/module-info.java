module org.simpleflatmapper.reflect {
        requires transitive org.simpleflatmapper.util;
        requires transitive org.simpleflatmapper.converter;

        exports org.simpleflatmapper.reflect;
        exports org.simpleflatmapper.reflect.meta;
        exports org.simpleflatmapper.reflect.asm;
        exports org.simpleflatmapper.reflect.instantiator;
        exports org.simpleflatmapper.reflect.getter;
        exports org.simpleflatmapper.reflect.setter;
        exports org.simpleflatmapper.reflect.primitive;
        exports org.simpleflatmapper.reflect.property;
        exports org.simpleflatmapper.ow2asm;
        exports org.simpleflatmapper.ow2asm.signature;

        uses org.simpleflatmapper.reflect.meta.AliasProviderProducer;
        uses org.simpleflatmapper.reflect.ReflectionService.ClassMetaFactoryProducer;
        uses org.simpleflatmapper.reflect.meta.AnnotationToPropertyServiceProducer;
}
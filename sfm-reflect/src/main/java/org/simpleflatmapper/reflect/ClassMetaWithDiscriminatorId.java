package org.simpleflatmapper.reflect;

import org.simpleflatmapper.reflect.meta.ClassMeta;

public class ClassMetaWithDiscriminatorId<T> {

    public final ClassMeta<T> classMeta;
    public final Object discriminatorId;

    public ClassMetaWithDiscriminatorId(ClassMeta<T> classMeta, Object discriminatorId) {
        this.classMeta = classMeta;
        this.discriminatorId = discriminatorId;
    }

    public ClassMetaWithDiscriminatorId<T> withReflectionService(ReflectionService reflectionService) {
        return new ClassMetaWithDiscriminatorId<T>(classMeta.withReflectionService(reflectionService), discriminatorId);
    }
}

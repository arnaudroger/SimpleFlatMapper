package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.util.Consumer;

import java.lang.annotation.Annotation;

public interface AnnotationToPropertyService {
    void generateProperty(Annotation annotation, Consumer<Object> consumer);
}

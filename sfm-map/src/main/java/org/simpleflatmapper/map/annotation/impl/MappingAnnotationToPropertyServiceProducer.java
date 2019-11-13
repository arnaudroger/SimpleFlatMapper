package org.simpleflatmapper.map.annotation.impl;

import org.simpleflatmapper.map.annotation.InferNull;
import org.simpleflatmapper.map.annotation.Key;
import org.simpleflatmapper.map.property.InferNullProperty;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.reflect.meta.AnnotationToPropertyService;
import org.simpleflatmapper.reflect.meta.AnnotationToPropertyServiceProducer;
import org.simpleflatmapper.util.Consumer;

import java.lang.annotation.Annotation;

public class MappingAnnotationToPropertyServiceProducer implements AnnotationToPropertyServiceProducer {
    @Override
    public void produce(Consumer<? super AnnotationToPropertyService> consumer) {
        consumer.accept(new MappingAnnotationToPropertyService());
    }

    private class MappingAnnotationToPropertyService implements AnnotationToPropertyService {
        @Override
        public void generateProperty(Annotation annotation, Consumer<Object> consumer) {
            if (Key.class.equals(annotation.annotationType())) {
                consumer.accept(KeyProperty.DEFAULT);
            }
            if (InferNull.class.equals(annotation.annotationType())) {
                consumer.accept(InferNullProperty.DEFAULT);
            }
        }
    }
}

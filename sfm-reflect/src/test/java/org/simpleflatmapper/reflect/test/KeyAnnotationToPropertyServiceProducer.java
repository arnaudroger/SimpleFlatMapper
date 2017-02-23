package org.simpleflatmapper.reflect.test;

import org.simpleflatmapper.reflect.meta.AnnotationToPropertyService;
import org.simpleflatmapper.reflect.meta.AnnotationToPropertyServiceProducer;
import org.simpleflatmapper.util.Consumer;

import java.lang.annotation.Annotation;

public class KeyAnnotationToPropertyServiceProducer implements AnnotationToPropertyServiceProducer {
    @Override
    public void produce(Consumer<? super AnnotationToPropertyService> consumer) {
        consumer.accept(new AnnotationToPropertyService() {
            @Override
            public void generateProperty(Annotation annotation, Consumer<Object> consumer) {
                if (KeyTest.class.equals(annotation.annotationType())) {
                    consumer.accept(new KeyTestProperty());
                }
            }
        });
    }
}

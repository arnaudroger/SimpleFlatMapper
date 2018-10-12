package org.simpleflatmapper.reflect.meta;


import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ProducerServiceLoader;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.ServiceLoader;

public final class AnnotationToPropertyUtil {

	private AnnotationToPropertyUtil() { }

	private static final AnnotationToPropertyService ANNOTATION_TO_PROPERTY_SERVICE = findAnnotationToProperty();

	private static AnnotationToPropertyService findAnnotationToProperty() {
		final ArrayList<AnnotationToPropertyService> providers = new ArrayList<AnnotationToPropertyService>();

		ProducerServiceLoader.produceFromServiceLoader(ServiceLoader.load(AnnotationToPropertyServiceProducer.class), new Consumer<AnnotationToPropertyService>() {
			@Override
			public void accept(AnnotationToPropertyService t) {
				providers.add(t);
			}
		});
		return aggregateAnnotationToProperty(providers.toArray(new AnnotationToPropertyService[0]));
	}

	public static AnnotationToPropertyService getAnnotationToPropertyService() {
		return ANNOTATION_TO_PROPERTY_SERVICE;
	}

	private static AnnotationToPropertyService aggregateAnnotationToProperty(AnnotationToPropertyService[] registered) {
		if (registered.length == 0) {
			return new DefaultAnnotationToPropertyService();
		} else if (registered.length == 1) {
			return registered[0];
		} else {
			return new ArrayAnnotationToPropertyService(registered);
		}
	}

	private static class DefaultAnnotationToPropertyService implements AnnotationToPropertyService {
		@Override
		public void generateProperty(Annotation annotation, Consumer<Object> consumer) {
		}
	}

	private static class ArrayAnnotationToPropertyService implements AnnotationToPropertyService {
		private final AnnotationToPropertyService[] registered;

		public ArrayAnnotationToPropertyService(AnnotationToPropertyService[] registered) {
			this.registered = registered;
		}

		@Override
		public void generateProperty(Annotation annotation, Consumer<Object> consumer) {
			for(AnnotationToPropertyService atp : registered) {
				atp.generateProperty(annotation, consumer);
			}
		}
	}
}

package org.simpleflatmapper.util;


import java.util.Iterator;
import java.util.ServiceLoader;


public class ProducerServiceLoader {

    @Deprecated
    public static <T, P extends Producer<T>> void produceFromServiceLoader(Class<P> producer, Consumer<T> consumer) {
        produceFromServiceLoader(ServiceLoader.load(producer), consumer);
    }
    
    public static  <T, P extends Producer<T>> void produceFromServiceLoader(ServiceLoader<P> serviceLoader, Consumer<T> consumer) {
        Iterator<P> iterator = serviceLoader.iterator();
        while(iterator.hasNext()) {
            try {
                iterator.next().produce(consumer);
            } catch (Throwable e) {
                System.err.println("Unexpected error on listing " + serviceLoader + " : "  + e);
                e.printStackTrace();
            }
        }
    }


    public interface Producer<T> {
        void produce(Consumer<? super T> consumer);
    }
}

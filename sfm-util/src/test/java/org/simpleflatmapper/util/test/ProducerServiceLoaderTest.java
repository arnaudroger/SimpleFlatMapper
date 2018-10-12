package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ProducerServiceLoader;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import static org.junit.Assert.*;

public class ProducerServiceLoaderTest {

    @Test
    public void testServiceLoader() throws Exception {
        final List<String> data = new ArrayList<String>();
        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) {
                data.add(s);
            }
        };


        URLClassLoader loader = new URLClassLoader(new URL[0], getClass().getClassLoader());

        ProducerServiceLoader.produceFromServiceLoader(ServiceLoader.load(ServiceProducer.class, loader), consumer);

        assertArrayEquals(new String[0], data.toArray(new String[0]));
        data.clear();


        loader = new URLClassLoader(new URL[]{new URL("file:src/test/resources/sl1/")}, getClass().getClassLoader());

        Thread.currentThread().setContextClassLoader(loader);

        ProducerServiceLoader.produceFromServiceLoader(ServiceLoader.load(ServiceProducer.class, loader), consumer);

        assertArrayEquals(new String[] {"ValidServiceProducer"}, data.toArray(new String[0]));
    }

}
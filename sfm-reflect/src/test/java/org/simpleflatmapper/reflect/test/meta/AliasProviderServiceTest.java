package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderService;
import org.simpleflatmapper.reflect.meta.ArrayAliasProvider;
import org.simpleflatmapper.reflect.meta.DefaultAliasProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.*;

public class AliasProviderServiceTest {

    @Test
    public void testServiceLoader() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MalformedURLException {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {

            Method findAliasProviders = AliasProviderService.class.getDeclaredMethod("findAliasProviders");
            findAliasProviders.setAccessible(true);

            Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0], getClass().getClassLoader()));
            AliasProvider defaultAliasProvider = (AliasProvider) findAliasProviders.invoke(null);
            assertTrue(defaultAliasProvider instanceof DefaultAliasProvider);


            URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file:src/test/resources/sl1/")}, getClass().getClassLoader());

            Thread.currentThread().setContextClassLoader(loader);

            AliasProvider oneALiasProvider = (AliasProvider) findAliasProviders.invoke(null);

            assertTrue(oneALiasProvider instanceof AliasProviderFactory1.AliasProvider1);

            loader = new URLClassLoader(new URL[]{new URL("file:src/test/resources/sl2/")}, getClass().getClassLoader());

            Thread.currentThread().setContextClassLoader(loader);
            AliasProvider multipleAliasProvider = (AliasProvider) findAliasProviders.invoke(null);

            assertTrue(multipleAliasProvider instanceof ArrayAliasProvider);

            ArrayAliasProvider arrayAliasProvider = (ArrayAliasProvider) multipleAliasProvider;

            AliasProvider[] providers = arrayAliasProvider.providers();

            assertEquals(2, providers.length);

            assertTrue(providers[0] instanceof AliasProviderFactory1.AliasProvider1);
            assertTrue(providers[1] instanceof AliasProviderFactory2.AliasProvider2);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }
}
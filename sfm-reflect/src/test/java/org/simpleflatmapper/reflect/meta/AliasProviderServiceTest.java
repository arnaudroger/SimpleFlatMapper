package org.simpleflatmapper.reflect.meta;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

import static org.junit.Assert.*;

public class AliasProviderServiceTest {

    @Test
    public void testServiceLoader() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MalformedURLException {
        Method findAliasProviders = AliasProviderService.class.getDeclaredMethod("findAliasProviders", ServiceLoader.class);
        findAliasProviders.setAccessible(true);

        ServiceLoader<AliasProviderFactory> serviceLoader = ServiceLoader.load(AliasProviderFactory.class, new URLClassLoader(new URL[0], getClass().getClassLoader()));

        AliasProvider defaultAliasProvider = (AliasProvider) findAliasProviders.invoke(null, serviceLoader);
        assertTrue(defaultAliasProvider instanceof DefaultAliasProvider);


        URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file:src/test/resources/sl1/")}, getClass().getClassLoader());
        AliasProvider oneALiasProvider = (AliasProvider) findAliasProviders.invoke(null, ServiceLoader.load(AliasProviderFactory.class, loader));

        assertTrue(oneALiasProvider instanceof  AliasProviderFactory1.AliasProvider1);

        loader = new URLClassLoader(new URL[]{new URL("file:src/test/resources/sl2/")}, getClass().getClassLoader());
        AliasProvider multipleAliasProvider = (AliasProvider) findAliasProviders.invoke(null, ServiceLoader.load(AliasProviderFactory.class, loader));

        assertTrue(multipleAliasProvider instanceof ArrayAliasProvider);

        ArrayAliasProvider arrayAliasProvider = (ArrayAliasProvider) multipleAliasProvider;

        AliasProvider[] providers = arrayAliasProvider.providers();

        assertEquals(2, providers.length);

        assertTrue(providers[0] instanceof AliasProviderFactory1.AliasProvider1);
        assertTrue(providers[1] instanceof AliasProviderFactory2.AliasProvider2);
    }
}